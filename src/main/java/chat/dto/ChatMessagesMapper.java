package chat.dto;

import chat.dto.interf.EntityDtoMapper;
import chat.model.ChatMessage;
import org.springframework.stereotype.Component;

@Component
public class ChatMessagesMapper implements EntityDtoMapper<ChatMessage, ChatMessageDto> {

    @Override
    public ChatMessageDto map (ChatMessage entity) {
        return ChatMessageDto
                .builder ()
                .tmpId (entity.getTmpId ())
                .id (entity.getId ())
                .text (entity.getText ())
                .status (entity.getStatus ())
                .fromUserId (entity.getFromUser ().getId ())
                .roomId (entity.getRoom ().getId ())
                .toUserId (entity.getToUser ().getId ())
                .createDateTime (entity.getCreateDateTime ())
                .build ();
    }
}
