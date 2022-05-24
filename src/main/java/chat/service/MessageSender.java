package chat.service;

import chat.model.User;
import chat.payload.response.ChatEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;

import static chat.config.WebSocketEndpoint.WEB_SOCKET_MESSAGES_ENDPOINT;

@Component
@AllArgsConstructor
@Slf4j
public class MessageSender {

    private final SimpUserRegistry simpUserRegistry;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public void sendChatEventToUser (User user, ChatEvent chatEvent) {
        final String id = user.getId ().toString ();
        final SimpUser simpUser = simpUserRegistry.getUser (id);
        if (simpUser != null) {
            simpMessagingTemplate.convertAndSendToUser (id, WEB_SOCKET_MESSAGES_ENDPOINT, chatEvent);
            log.info ("Sending an event {} to user id {}", chatEvent, id);
        }
    }
}
