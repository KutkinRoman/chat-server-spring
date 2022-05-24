package chat.security.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import static chat.security.jwt.TokenType.BEARER;

@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.access.secret}")
    private String jwtAccessSecret;

    public Optional<String> parseJwtFromHeader (HttpServletRequest request) {
        String headerAuth = request.getHeader (HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText (headerAuth) && headerAuth.startsWith (BEARER.getHeader ())) {
            String token = headerAuth.substring (BEARER.getHeader ().length (), headerAuth.length ());
            return Optional.of (token);
        }
        return Optional.empty ();
    }

    public Claims getClaimsFromToken (String token) {
        return getClaimsFromToken (token, jwtAccessSecret);
    }

    public String getUserNameFromJwtAccessToken (String accessToken) {
        return getClaimsFromToken (accessToken, jwtAccessSecret).getSubject ();
    }

    public boolean validateJwtAccessToken (String accessToken) {
        return validateJwtToken (accessToken, jwtAccessSecret);
    }

    private boolean validateJwtToken (String token, String secret) {
        try {
            return getClaimsFromToken (token, secret).getExpiration ().after (new Date ());
        } catch (SignatureException e) {
            log.error ("Invalid JWT signature: {}", e.getMessage ());
        } catch (MalformedJwtException e) {
            log.error ("Invalid JWT token: {}", e.getMessage ());
        } catch (ExpiredJwtException e) {
            log.error ("JWT token is expired: {}", e.getMessage ());
        } catch (UnsupportedJwtException e) {
            log.error ("JWT token is unsupported: {}", e.getMessage ());
        } catch (IllegalArgumentException e) {
            log.error ("JWT claims string is empty: {}", e.getMessage ());
        }
        return false;
    }

    private Claims getClaimsFromToken (String token, String secret) {
        String key = Base64.getEncoder ().encodeToString (secret.getBytes ());
        return Jwts.parserBuilder ()
                .setSigningKey (key)
                .build ()
                .parseClaimsJws (token)
                .getBody ();
    }

}
