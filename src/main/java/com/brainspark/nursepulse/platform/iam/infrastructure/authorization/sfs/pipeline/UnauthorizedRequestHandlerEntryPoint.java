package com.brainspark.nursepulse.platform.iam.infrastructure.authorization.sfs.pipeline;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Unauthorized Request Handler.
 * <p>
 * This class is responsible for handling unauthorized requests.
 * It is used by the Spring Security framework to handle unauthorized requests.
 * It implements the AuthenticationEntryPoint interface.
 * </p>
 * @see AuthenticationEntryPoint
 */

@Component
@Slf4j
public class UnauthorizedRequestHandlerEntryPoint implements AuthenticationEntryPoint {

    /**
     * This method is called by the Spring Security framework when an unauthorized request is detected.
     * @param request The request that caused the exception
     * @param response The response that will be sent to the client
     * @param authenticationException The exception that caused the invocation
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException {
        log.debug("Unauthorized request to {}: {}", request.getRequestURI(), authenticationException.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("""
                {"code":"UNAUTHORIZED","message":"Authentication is required"}
                """);
    }
}
