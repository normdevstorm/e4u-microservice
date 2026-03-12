package com.renting.authentication_service.config.security.filter;

import com.renting.authentication_service.common.constants.ConstantManager;
import com.renting.authentication_service.common.exception.custom.CustomJwtException;
import com.renting.authentication_service.common.exception.handler.GlobalExceptionHandler;
import com.renting.authentication_service.entity.User;
import com.renting.authentication_service.repository.UserRepository;
import com.renting.authentication_service.service.JwtService;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.annotation.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Log4j2
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final GlobalExceptionHandler globalExceptionHandler;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository,
            @org.springframework.beans.factory.annotation.Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver,
            GlobalExceptionHandler globalExceptionHandler) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.globalExceptionHandler = globalExceptionHandler;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // TODO: ADD PATTERN MATCHER HERE FOR SHOULD-NOT-FILTER ENDPOINTS
        String path = request.getServletPath();
        return path.matches(ConstantManager.SHOULD_NOT_FILTER_JWT_PATHS_REGEX);

    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = extractToken(request, response, filterChain);
        try {
            if (authHeader == null)
                throw new CustomJwtException("Missing JWT Token");
            final String jwt = authHeader.substring(7);

            final String username = jwtService.extractUsername(jwt, false);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (username != null && authentication == null) {
                User user = userRepository.findByUsername(username).orElseThrow();

                if (jwtService.isTokenValid(jwt, false)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

                ThreadContext.put("userId", "[ userId: " + user.getUserId() + " ]");
                log.info("User authenticated");
                ThreadContext.clearMap();
            }

            filterChain.doFilter(request, response);
        } catch (MalformedJwtException exception) {
            logger.error(exception.getMessage());
            globalExceptionHandler.malformedJwtExceptionHandler(exception);
        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }

    @Nullable
    private static String extractToken(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return null;
        }
        return authHeader;
    }
}
