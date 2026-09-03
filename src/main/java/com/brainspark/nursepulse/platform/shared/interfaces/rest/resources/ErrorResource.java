package com.brainspark.nursepulse.platform.shared.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.jspecify.annotations.Nullable;
@JsonInclude(JsonInclude.Include.NON_NULL)

public record ErrorResource(String code,String message,@Nullable String details) {

        /**
         * Creates an ErrorResource from code and message.
         */
    public ErrorResource(String code, String message) {
            this(code, message, null);
        }
}
