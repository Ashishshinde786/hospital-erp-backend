package com.hospital.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * ---------------------------------------------------------
 * CLASS: SwaggerConfig
 * ---------------------------------------------------------
 *
 * PURPOSE:
 * Configures Swagger UI and OpenAPI documentation
 * for the Hospital ERP Backend.
 *
 * After adding this:
 *
 * Swagger UI available at:
 * http://localhost:8080/swagger-ui/index.html
 *
 * OpenAPI JSON available at:
 * http://localhost:8080/v3/api-docs
 *
 * ---------------------------------------------------------
 * JWT SUPPORT
 * ---------------------------------------------------------
 *
 * This config adds a JWT Bearer Token input
 * directly inside Swagger UI.
 *
 * So you can:
 * 1. Login via /api/auth/login
 * 2. Copy the token
 * 3. Click "Authorize" in Swagger UI
 * 4. Paste token
 * 5. All secured APIs will work from Swagger UI
 *
 * ---------------------------------------------------------
 */
@Configuration
public class SwaggerConfig {

    /*
     * Name of the security scheme.
     * Must match what we use in SecurityRequirement.
     */
    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    /*
     * ---------------------------------------------------------
     * OPENAPI BEAN
     * ---------------------------------------------------------
     *
     * Defines:
     * - API title and description
     * - Version
     * - Contact info
     * - License
     * - JWT security scheme
     * - Global security requirement
     */
    @Bean
    public OpenAPI hospitalErpOpenAPI() {

        return new OpenAPI()

                /*
                 * ---------------------------------------------------------
                 * API INFO
                 * ---------------------------------------------------------
                 *
                 * Shown at the top of Swagger UI page.
                 */
                .info(new Info()

                        .title("Hospital ERP Backend API")

                        .description(
                                "REST API documentation for Hospital ERP System.\n\n" +
                                "**Modules covered:**\n" +
                                "- 🔐 Authentication (Login / Register)\n" +
                                "- 🧑‍⚕️ Patient Management\n" +
                                "- 👨‍⚕️ Doctor Management\n" +
                                "- 📅 Appointment Scheduling\n" +
                                "- 💊 Pharmacy & Medicine Inventory\n" +
                                "- 🧾 Billing & Invoice\n\n" +
                                "**How to use secured APIs:**\n" +
                                "1. Use `/api/auth/login` to get JWT token\n" +
                                "2. Click **Authorize** button above\n" +
                                "3. Enter: `<your_token>` (without Bearer prefix)\n" +
                                "4. All secured endpoints will now work"
                        )

                        .version("v1.0.0")

                        .contact(new Contact()
                                .name("Hospital ERP Team")
                                .email("admin@hospital.com")
                                .url("http://localhost:8080")
                        )

                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")
                        )
                )

                /*
                 * ---------------------------------------------------------
                 * SECURITY SCHEME
                 * ---------------------------------------------------------
                 *
                 * Tells Swagger:
                 * - Use Bearer token
                 * - In Authorization header
                 * - JWT format
                 *
                 * This adds "Authorize" button in Swagger UI.
                 */
                .components(new Components()

                        .addSecuritySchemes(SECURITY_SCHEME_NAME,

                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description(
                                                "Enter your JWT token below.\n" +
                                                "Get it from POST /api/auth/login response."
                                        )
                        )
                )

                /*
                 * ---------------------------------------------------------
                 * GLOBAL SECURITY REQUIREMENT
                 * ---------------------------------------------------------
                 *
                 * Applies JWT auth requirement to ALL APIs globally.
                 *
                 * Individual public APIs (login/register) will
                 * override this using @SecurityRequirements({}) annotation.
                 */
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(SECURITY_SCHEME_NAME)
                );
    }
}