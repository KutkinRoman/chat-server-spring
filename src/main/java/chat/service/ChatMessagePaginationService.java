package chat.service;

import chat.dto.ChatMessageDto;
import chat.model.ChatRoom;

import java.util.List;

public interface ChatMessagePaginationService {

    List<ChatMessageDto> getChatMessagesByRoom(ChatRoom chatRoom,  Long lessMessageId, Integer limit);
}
