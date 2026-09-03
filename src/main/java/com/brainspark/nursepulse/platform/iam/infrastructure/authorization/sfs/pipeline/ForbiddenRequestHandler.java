package com.brainspark.nursepulse.platform.iam.infrastructure.authorization.sfs.pipeline;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Forbidden Request Handler.
 * <p>
 * This class writes the 403 response directly when an authenticated user lacks
 * the required role. Writing the body directly avoids the container error-page
 * dispatch, which would otherwise be intercepted by the security filter chain
 * and masked as a 401 response.
 * </p>
 * @see AccessDeniedHandler
 */
@Component
@Slf4j
public class ForbiddenRequestHandler implements AccessDeniedHandler {

    /**
     * This method is called by the Spring Security framework when an authenticated
     * request is denied by the authorization rules.
     * @param request The request that caused the exception
     * @param response The response that will be sent to the client
     * @param accessDeniedException The exception that caused the invocation
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        log.debug("Forbidden request to {}: {}", request.getRequestURI(), accessDeniedException.getMessage());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("""
                {"code":"FORBIDDEN","message":"You do not have permission to perform this action"}
                """);
    }
}
