package chat.service;

import chat.payload.request.NewChatMessageRequest;

public interface ChatMessageService {

    void handleNewMessage (NewChatMessageRequest newChatMessageRequest);

    void handleMessageReceived (Long userId, Long messageId);

    void handleMessageRead (Long userId, Long messageId);

    void handleMessageDelete (Long userId, Long messageId);

}
