package com.e4u.lesson_service.common.filter;

import com.e4u.lesson_service.common.logging.MdcConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter to set up MDC context for each request.
 * This ensures all logs within a request have consistent trace information.
 * Should run before other filters to capture complete request lifecycle.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MdcFilter extends OncePerRequestFilter {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    // OpenTelemetry W3C Trace Context headers
    private static final String TRACEPARENT_HEADER = "traceparent";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            setupMdc(request, response);
            filterChain.doFilter(request, response);
        } finally {
            // Clean up MDC to prevent memory leaks in thread pools
            MDC.clear();
        }
    }

    private void setupMdc(HttpServletRequest request, HttpServletResponse response) {
        // Extract or generate trace ID (OpenTelemetry compatible)
        String traceId = extractOrGenerateTraceId(request);
        String spanId = generateSpanId();
        String requestId = extractOrGenerateRequestId(request);
        String correlationId = extractOrGenerateCorrelationId(request);

        // Set MDC values
        MDC.put(MdcConstants.TRACE_ID, traceId);
        MDC.put(MdcConstants.SPAN_ID, spanId);
        MDC.put(MdcConstants.REQUEST_ID, requestId);
        MDC.put(MdcConstants.CORRELATION_ID, correlationId);
        MDC.put(MdcConstants.HTTP_METHOD, request.getMethod());
        MDC.put(MdcConstants.HTTP_URI, request.getRequestURI());
        MDC.put(MdcConstants.CLIENT_IP, getClientIp(request));
        MDC.put(MdcConstants.USER_AGENT, request.getHeader("User-Agent"));

        // Set service context
        MDC.put(MdcConstants.SERVICE_NAME, "lesson-service");

        // Add trace headers to response for distributed tracing
        response.setHeader(TRACE_ID_HEADER, traceId);
        response.setHeader(REQUEST_ID_HEADER, requestId);
        response.setHeader(CORRELATION_ID_HEADER, correlationId);
    }

    /**
     * Extract trace ID from OpenTelemetry traceparent header or generate new one.
     * traceparent format: version-traceId-parentId-flags (e.g.,
     * 00-0af7651916cd43dd8448eb211c80319c-b7ad6b7169203331-01)
     */
    private String extractOrGenerateTraceId(HttpServletRequest request) {
        // Try OpenTelemetry W3C Trace Context header
        String traceparent = request.getHeader(TRACEPARENT_HEADER);
        if (traceparent != null && traceparent.length() >= 55) {
            String[] parts = traceparent.split("-");
            if (parts.length >= 2) {
                return parts[1];
            }
        }

        // Try custom trace header
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (traceId != null && !traceId.isEmpty()) {
            return traceId;
        }

        // Generate new trace ID (32 hex characters for OpenTelemetry compatibility)
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Generate span ID (16 hex characters for OpenTelemetry compatibility)
     */
    private String generateSpanId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    private String extractOrGenerateRequestId(HttpServletRequest request) {
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        return (requestId != null && !requestId.isEmpty())
                ? requestId
                : UUID.randomUUID().toString();
    }

    private String extractOrGenerateCorrelationId(HttpServletRequest request) {
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        return (correlationId != null && !correlationId.isEmpty())
                ? correlationId
                : UUID.randomUUID().toString();
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Skip MDC setup for static resources
        return path.startsWith("/static/")
                || path.startsWith("/favicon.ico")
                || path.endsWith(".css")
                || path.endsWith(".js")
                || path.endsWith(".png")
                || path.endsWith(".jpg");
    }
}
