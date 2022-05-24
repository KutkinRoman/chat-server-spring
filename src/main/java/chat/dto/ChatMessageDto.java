package chat.dto;

import chat.model.ChatMessage.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDto {

    private String tmpId;

    private Long id;

    private String text;

    private Long fromUserId;

    private Long toUserId;

    private Long roomId;

    private Status status;

    private LocalDateTime createDateTime;

}
