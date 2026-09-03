package com.brainspark.nursepulse.platform.shared.infrastructure.documentation.openapi.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration class for OpenAPI/Swagger documentation.
 *
 * This class defines metadata about the API (title, description, version), contact information,
 * licensed terms, the current server endpoint, and security schemes
 * used for authentication.
 *
 * The configuration uses Spring beans and property injection to populate OpenAPI metadata from
 * application properties, enabling centralized management of API documentation details.
 *
 * This configuration is automatically picked up by Springdoc-OpenAPI and exposed through
 * Swagger UI and OpenAPI endpoints.
 *
 */
@Configuration
public class OpenApiConfiguration {
    // Properties - Injected from application.properties

    /**
     * Application name extracted from spring.application.name property.
     * Used as the OpenAPI specification title.
     */
    @Value("${spring.application.name}")
    String applicationName;

    /**
     * Application description extracted from documentation.application.description property.
     * Provides a detailed explanation of the API's purpose and functionality.
     */
    @Value("${documentation.application.description}")
    String applicationDescription;

    /**
     * Application version extracted from documentation.application.version property.
     * Matches the current release version of the API.
     */
    @Value("${documentation.application.version}")
    String applicationVersion;


    // Bean Methods - Configuration

    /**
     * Creates and configures the OpenAPI specification bean for this application.
     *
     * This method builds the following OpenAPI documentation:
     * <ul>
     *   <li><b>API Information:</b> Title, description, version, contact details, and license</li>
     *   <li><b>Server Endpoint:</b> Uses the same origin that serves Swagger UI</li>
     *   <li><b>Security Scheme:</b> Configures JWT Bearer token authentication for all endpoints</li>
     * </ul>
     *
     * The OpenAPI object is automatically used by API documentation tools such as Swagger to generate interactive API
     * documentation and client SDKs.
     *
     * @return a fully configured {@link OpenAPI} bean containing API specification metadata
     */
    @Bean
    public OpenAPI learningPlatformOpenApi() {
        var openApi = new OpenAPI();

        // Configure API Information
        openApi
                .info(new Info()
                        .title(this.applicationName)
                        .description(this.applicationDescription)
                        .version(this.applicationVersion)
                        // Add contact information for API support and inquiries
                        .contact(new Contact()
                                .name("BrainSpark NursePulse Support")
                                .email("support@brainspark-nursepulse.com")
                        )
                        // Define the license under which the API is published
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));

        // A relative URL keeps Swagger requests on the same origin that served
        // the documentation. It works in Railway and locally without CORS or
        // environment-specific hostnames.
        openApi.servers(List.of(
                new Server()
                        .url("/")
                        .description("Current environment")
        ));

        // Configure Security Scheme

        // Define the security scheme name used throughout the API
        final String securitySchemeName = "bearerAuth";

        // Add JWT Bearer token security requirement to all API endpoints
        openApi.addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        // Define the JWT Bearer authentication scheme
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                // Indicate the use of bearer tokens
                                .scheme("bearer")
                                // Specify the token format as JWT
                                .bearerFormat("JWT")
                                .description("JWT Bearer token for API authentication")));

        return openApi;
    }
}
