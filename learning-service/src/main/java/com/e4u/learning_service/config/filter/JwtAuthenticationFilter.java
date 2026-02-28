package com.e4u.learning_service.config.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.annotation.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.e4u.learning_service.common.constants.Constant;
import com.e4u.learning_service.common.exception.AppException;
import com.e4u.learning_service.common.exception.ErrorCode;
import com.e4u.learning_service.common.exception.GlobalExceptionHandler;
import com.e4u.learning_service.dtos.response.UserModelFromJwtPayload;
import com.e4u.learning_service.services.JwtService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Log4j2
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver,
            GlobalExceptionHandler globalExceptionHandler) {
        this.jwtService = jwtService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // TODO: ADD PATTERN MATCHER HERE FOR SHOULD-NOT-FILTER ENDPOINTS
        String path = request.getServletPath();
        return path.matches(Constant.SHOULD_NOT_FILTER_JWT_PATHS_REGEX);

    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = extractToken(request, response, filterChain);
        try {
            if (authHeader == null)
                throw new AppException(ErrorCode.UNAUTHORIZED, "Missing JWT Token");
            final String jwt = authHeader.substring(7);

            final Claims claims = jwtService.extractClaim(jwt);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (claims != null && authentication == null) {
                UserModelFromJwtPayload user = new UserModelFromJwtPayload();
                user.setId(UUID.fromString(claims.get("id", String.class)));
                user.setUsername(claims.get("username", String.class));
                user.setAuthorities(
                        claims.get("authorities", List.class).stream().map(authority -> new SimpleGrantedAuthority(
                                ((LinkedHashMap<String, String>) authority).get("authority"))).toList());
                user.setRole(claims.get("role", String.class));

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("JWT Token is valid, setting authentication in context for user: {}", user.getUsername());
            }

            filterChain.doFilter(request, response);
        } catch (MalformedJwtException exception) {
            logger.error(exception.getMessage());
            throw new AppException(ErrorCode.UNAUTHORIZED, "Invalid JWT Token");
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
