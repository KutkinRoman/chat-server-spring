package chat.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class CorsConfig implements WebMvcConfigurer {

    @Value("${origins-front}")
    private String[] originsFront;

    @Override
    public void addCorsMappings (CorsRegistry registry) {
        registry
                .addMapping ("/**")
                .allowedOrigins (originsFront)
                .allowedMethods ("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        ;
    }
}
