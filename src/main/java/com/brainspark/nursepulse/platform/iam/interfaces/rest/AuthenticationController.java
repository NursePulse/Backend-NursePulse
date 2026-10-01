package com.brainspark.nursepulse.platform.iam.interfaces.rest;

import com.brainspark.nursepulse.platform.iam.application.commandservices.UserCommandService;
import com.brainspark.nursepulse.platform.iam.domain.model.aggregates.User;
import com.brainspark.nursepulse.platform.iam.domain.model.commands.VerifyEmailCommand;
import com.brainspark.nursepulse.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.brainspark.nursepulse.platform.iam.interfaces.rest.resources.SignInResource;
import com.brainspark.nursepulse.platform.iam.interfaces.rest.resources.SignUpResource;
import com.brainspark.nursepulse.platform.iam.interfaces.rest.resources.UserResource;
import com.brainspark.nursepulse.platform.iam.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import com.brainspark.nursepulse.platform.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import com.brainspark.nursepulse.platform.iam.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import com.brainspark.nursepulse.platform.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;
import com.brainspark.nursepulse.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * AuthenticationController
 * <p>
 *     This controller is responsible for handling authentication requests.
 *     It exposes two endpoints:
 *     <ul>
 *         <li>POST /api/v1/authentication/sign-in</li>
 *         <li>POST /api/v1/authentication/sign-up</li>
 *     </ul>
 * </p>
 */
@RestController
@RequestMapping(value = "/api/v1/authentication", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "Authentication and user registration endpoints")
@SecurityRequirements
public class AuthenticationController {
    private final UserCommandService userCommandService;
    private final String frontendUrl;

    public AuthenticationController(
            UserCommandService userCommandService,
            @Value("${app.frontend-url:http://localhost:4200}") String frontendUrl
    ) {
        this.userCommandService = userCommandService;
        this.frontendUrl = frontendUrl;
    }

    /**
     * Handles the sign-in request.
     * @param signInResource the sign-in request body with username and password.
     * @return the authenticated user resource with JWT token.
     */
    @PostMapping("/sign-in")
    @Operation(
        summary = "User sign-in",
        description = "Authenticates a user with provided credentials and returns JWT token for subsequent requests."
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200",
                description = "User authenticated successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AuthenticatedUserResource.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid credentials or malformed request",
                content = @Content(mediaType = "application/json")
            ),
    })
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInResource signInResource) {
        var signInCommand = SignInCommandFromResourceAssembler.toCommandFromResource(signInResource);
        var result = userCommandService.handle(signInCommand);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                auth -> AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(auth.getLeft(), auth.getRight()),
                HttpStatus.OK
        );
    }

    /**
     * Handles the sign-up request.
     * @param signUpResource the sign-up request body with username, password and clinical role.
     * @return the created user resource with the selected nurse or doctor role.
     */
    @PostMapping("/sign-up")
    @Operation(
        summary = "User registration",
        description = "Creates a clinical staff account with ROLE_NURSE or ROLE_DOCTOR. "
                + "ROLE_ADMIN is never accepted through public registration."
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "201",
                description = "User created successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResource.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid input data or username already exists",
                content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                responseCode = "409",
                description = "Conflict - username already taken",
                content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpResource signUpResource) {
        var signUpCommand = SignUpCommandFromResourceAssembler.toCommandFromResource(signUpResource);
        var result = userCommandService.handle(signUpCommand);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                UserResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );

    }

    /**
     * Handles the email verification link sent at sign-up.
     * Renders a plain HTML confirmation page, since this endpoint is opened
     * directly from the user's email client, not called by the frontend app.
     *
     * @param token the verification token issued at sign-up.
     * @return an HTML page confirming success or explaining the failure.
     */
    @GetMapping(value = "/verify-email", produces = MediaType.TEXT_HTML_VALUE)
    @Operation(
        summary = "Confirm account email",
        description = "Marks the account's email as verified using the token sent by email at sign-up."
    )
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        var result = userCommandService.handle(new VerifyEmailCommand(token));
        return switch (result) {
            case Result.Success<User, ApplicationError> ignored -> ResponseEntity.ok(renderVerificationPage(
                    "Cuenta verificada",
                    "Tu correo fue confirmado con exito. Ya puedes iniciar sesion en NursePulse.",
                    true
            ));
            case Result.Failure<User, ApplicationError> failure -> ResponseEntity.badRequest().body(renderVerificationPage(
                    "No se pudo verificar tu cuenta",
                    failure.error().details() != null ? failure.error().details() : failure.error().message(),
                    false
            ));
        };
    }

    private String renderVerificationPage(String title, String message, boolean success) {
        var accentColor = success ? "#0f9d58" : "#d93025";
        return """
                <!doctype html>
                <html lang="es">
                <head>
                  <meta charset="utf-8" />
                  <title>%s · NursePulse</title>
                  <style>
                    body { font-family: -apple-system, Arial, sans-serif; background: #f5f7fb; display: flex; align-items: center; justify-content: center; height: 100vh; margin: 0; }
                    .card { background: #fff; border-radius: 12px; padding: 40px; max-width: 420px; text-align: center; box-shadow: 0 10px 30px rgba(0,0,0,0.08); }
                    h1 { color: %s; font-size: 22px; margin-bottom: 12px; }
                    p { color: #333; line-height: 1.5; }
                    a.button { display: inline-block; margin-top: 20px; padding: 12px 24px; background-color: #0052cc; color: #fff; text-decoration: none; border-radius: 6px; font-weight: bold; }
                  </style>
                </head>
                <body>
                  <div class="card">
                    <h1>%s</h1>
                    <p>%s</p>
                    <a class="button" href="%s/sign-in">Ir a iniciar sesion</a>
                  </div>
                </body>
                </html>
                """.formatted(title, accentColor, title, message, frontendUrl);
    }
}
