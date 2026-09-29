package co.edu.escuelaing.ecifit.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI ecifitOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ECI FIT API")
                        .version("1.0.0")
                        .description("API para la plataforma de gamificación y entrenamiento ECI FIT"));
    }
}