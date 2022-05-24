package chat.security.service;

import chat.security.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtils jwtUtils;

    public void authenticateUser (HttpServletRequest request, String token) {
        Claims claims = jwtUtils.getClaimsFromToken (token);
        UserDetails user = mapClaimsToSecurityUser (claims);
        UsernamePasswordAuthenticationToken authentication = mapUserDetailsToAuthentication (user);
        authentication.setDetails (new WebAuthenticationDetailsSource ().buildDetails (request));
        SecurityContextHolder.getContext ().setAuthentication (authentication);
    }

    public UserDetails mapClaimsToSecurityUser (Claims claims) {
        String username = claims.getSubject ();
        List<String> roles = ( List<String> ) claims.get ("roles");
        List<GrantedAuthority> authorities = roles.stream ().map (SimpleGrantedAuthority::new).collect (Collectors.toList ());
        return new User (username, "", authorities)
        ;
    }

    public UsernamePasswordAuthenticationToken mapUserDetailsToAuthentication (UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken (userDetails, null, userDetails.getAuthorities ());
    }
}
