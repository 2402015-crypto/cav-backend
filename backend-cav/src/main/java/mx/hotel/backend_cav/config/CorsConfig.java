package mx.hotel.backend_cav.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins}") String allowedOrigins) {
        CorsConfiguration configuration = new CorsConfiguration();
            String origins = allowedOrigins == null ? "" : allowedOrigins;
            List<String> configuredOrigins = Arrays.stream(origins.split(","))
                .map(origin -> origin == null ? "" : origin.trim())
                .filter(origin -> !origin.isBlank())
                .toList();

        configuration.setAllowedOriginPatterns(
                configuredOrigins.isEmpty()
                        ? List.of("*")
                        : java.util.stream.Stream.concat(
                                configuredOrigins.stream(),
                                java.util.stream.Stream.of("*"))
                                .distinct()
                                .toList());
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "Origin"));
        configuration.setExposedHeaders(Arrays.asList("Location"));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
