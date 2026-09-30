package co.edu.eci.dosw.ecifit.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI eciFitOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ECI FIT API")
                        .version("1.0.0")
                        .description("API REST institucional para la plataforma de gamificación y actividad física ECI FIT - Escuela Colombiana de Ingeniería Julio Garavito.")
                        .contact(new Contact()
                                .name("Equipo DOSW - ECI FIT")
                                .email("dosw.ecifit@escuelaing.edu.co"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
