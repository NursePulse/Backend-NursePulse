package com.brainspark.nursepulse.platform.auditlogs.infrastructure.pdf;

/**
 * Thrown when the audit log PDF document could not be rendered.
 */
public class AuditLogPdfRenderingException extends RuntimeException {
    public AuditLogPdfRenderingException(Throwable cause) {
        super("Failed to render the audit log PDF export", cause);
    }
}
