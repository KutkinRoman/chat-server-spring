package chat.security.config;

import chat.security.service.AuthTokenFilter;
import chat.security.service.UnauthorizedHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter  {

    private final AuthTokenFilter authenticationJwtTokenFilter;
    private final UnauthorizedHandler unauthorizedHandler;

    @Override
    protected void configure (HttpSecurity http) throws Exception {
        http
                .exceptionHandling ().authenticationEntryPoint (unauthorizedHandler)
                .and ()
                .sessionManagement ().sessionCreationPolicy (SessionCreationPolicy.STATELESS)
                .and ()
                .cors ()
                .and ()
                .csrf ().disable ()
                .formLogin ().disable ()
                .httpBasic ().disable ()
                .authorizeRequests ()
                .anyRequest ().permitAll ()
                .and ()
                .addFilterBefore (authenticationJwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
        ;
    }

}
