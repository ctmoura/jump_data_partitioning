package br.com.ctmoura.jump_dp.interceptor;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestIdInterceptor implements HandlerInterceptor {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Get the request ID from the header or generate a new one
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        // Add request ID to the response header
        response.setHeader(REQUEST_ID_HEADER, requestId);

        // Add request ID to MDC (Mapped Diagnostic Context) for logging
        MDC.put(REQUEST_ID_HEADER, requestId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        // Clear MDC to avoid memory leaks
        MDC.remove(REQUEST_ID_HEADER);
    }
}
