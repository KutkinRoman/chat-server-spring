package chat.config;

import chat.security.jwt.JwtUtils;
import chat.security.service.AuthService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import static chat.config.WebSocketEndpoint.WEB_SOCKET_MESSAGES_ENDPOINT;
import static chat.config.WebSocketEndpoint.WEB_SOCKET_USER_ENDPOINT;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
@RequiredArgsConstructor
public class WebSocketAuthenticationConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtils jwtUtils;
    private final AuthService authService;

    @Override
    public void configureClientInboundChannel (ChannelRegistration registration) {

        registration.interceptors (new ChannelInterceptor () {
            @Override
            public Message<?> preSend (Message<?> message, MessageChannel channel) {

                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor (message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals (accessor.getCommand ())) {

                    String accessToken = accessor.getNativeHeader ("X-Authorization").get (0).split (" ")[1];

                    if (jwtUtils.validateJwtAccessToken (accessToken)) {
                        final Claims claims = jwtUtils.getClaimsFromToken (accessToken);
                        final UserDetails userDetails = authService.mapClaimsToSecurityUser (claims);
                        UsernamePasswordAuthenticationToken authentication = authService.mapUserDetailsToAuthentication (userDetails);
                        SecurityContextHolder.getContext ().setAuthentication (authentication);
                        accessor.setUser (authentication);
                    }
                }

                if (accessor.getDestination () != null && accessor.getUser () != null && accessor.getDestination ().startsWith (WEB_SOCKET_USER_ENDPOINT)) {
                    final String endpoint = WEB_SOCKET_USER_ENDPOINT + "/" + accessor.getUser ().getName () + WEB_SOCKET_MESSAGES_ENDPOINT;
                    if (!endpoint.equals (accessor.getDestination ())) {
                        accessor.setUser (null);
                    }
                }

                return message;
            }
        });
    }
}