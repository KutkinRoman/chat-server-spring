package chat.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomDto {

    private Long id;

    private UserDto chatUser;

    private LocalDateTime lastEventDateTime;

    private ChatMessageDto lastMessage;

    private Long totalMessages;

    private Long totalNewMessages;

    private Long totalNewMessagesUser;

}
