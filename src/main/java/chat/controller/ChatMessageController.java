package chat.controller;

import chat.payload.request.NewChatMessageRequest;
import chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatMessageController extends ExceptionHandlerController {

    private final ChatMessageService chatMessageService;

    @MessageMapping("/messages/new")
    public void newMessage (NewChatMessageRequest newChatMessageRequest, Authentication authentication) {
        newChatMessageRequest.setFromUserId (Long.parseLong (authentication.getName ()));
        chatMessageService.handleNewMessage (newChatMessageRequest);
    }

    @MessageMapping("/messages/received")
    public void messageReceived (Long messageId, Authentication authentication) {
        Long userId = Long.parseLong (authentication.getName ());
        chatMessageService.handleMessageReceived (userId, messageId);
    }

    @MessageMapping("/messages/read")
    public void messageRead (Long messageId, Authentication authentication) {
        Long userId = Long.parseLong (authentication.getName ());
        chatMessageService.handleMessageRead (userId, messageId);
    }

    @MessageMapping("/messages/delete")
    public void messageDelete (Long messageId, Authentication authentication) {
        Long userId = Long.parseLong (authentication.getName ());
        chatMessageService.handleMessageDelete (userId, messageId);
    }

}
