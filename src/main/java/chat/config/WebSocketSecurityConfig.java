package chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import static chat.config.WebSocketEndpoint.*;

/**
 * @link https://developer.okta.com/blog/2019/10/09/java-spring-websocket-tutorial
 */

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Value ("${origins-front}")
    private String[] originsFront;

    @Override
    public void configureMessageBroker (MessageBrokerRegistry config) {
        config.enableSimpleBroker (WEB_SOCKET_USER_ENDPOINT);
        config.setApplicationDestinationPrefixes (WEB_SOCKET_APP_ENDPOINT);
    }

    @Override
    public void registerStompEndpoints (StompEndpointRegistry registry) {
        registry
                .addEndpoint (WEB_SOCKET_ENDPOINT)
                .setAllowedOrigins (originsFront)
                .withSockJS ()
        ;
    }

    @Override
    protected void configureInbound (MessageSecurityMetadataSourceRegistry messages) {
        messages
                .anyMessage ()
                .authenticated ()
        ;
    }

    @Override
    protected boolean sameOriginDisabled () {
        return true;
    }

}