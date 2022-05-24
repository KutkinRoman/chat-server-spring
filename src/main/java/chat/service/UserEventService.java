package chat.service;

import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

public interface UserEventService {

    void handleSessionConnected (SessionConnectEvent event);

    void handleSessionDisconnect (SessionDisconnectEvent event);

}
