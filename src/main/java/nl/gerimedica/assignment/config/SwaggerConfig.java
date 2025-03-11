package nl.gerimedica.assignment.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Hospital Management API",
                version = "1.0",
                description = "API for managing hospital appointments and patients"
        )
)
public class SwaggerConfig {
}
