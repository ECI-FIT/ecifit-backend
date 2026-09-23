package co.edu.escuelaing.ecifit.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "ECIFIT API",
        version = "1.0.0",
        description = "API para la plataforma de gamificación ECI FIT"
    )
)
public class OpenApiConfig {
}
