package mx.hotel.backend_cav.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI cavOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Control de Acceso Vehicular API")
                        .version("v1")
                        .description("Contrato HTTP base del sistema CAV."));
    }
}