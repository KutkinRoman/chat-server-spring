package chat.security.service;

import chat.security.jwt.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
@AllArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final AuthService authService;

    @Override
    protected void doFilterInternal (HttpServletRequest request, HttpServletResponse response, FilterChain filter)
            throws ServletException, IOException {

        try {
            Optional<String> jwt = jwtUtils.parseJwtFromHeader (request);
            if (jwt.isPresent () && jwtUtils.validateJwtAccessToken (jwt.get ())) {
                authService.authenticateUser (request, jwt.get ());
            }
        } catch (Exception e) {
            logger.error ("Cannot set user authentication: {}", e);
        }

        filter.doFilter (request, response);
    }

}
