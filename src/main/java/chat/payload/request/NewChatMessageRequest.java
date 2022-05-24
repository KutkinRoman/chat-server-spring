package chat.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@ToString
public class NewChatMessageRequest {

    @NotNull
    private String tmpId;

    @NotNull
    private String text;

    @NotNull
    private Long toUserId;

    @NotNull
    private Long roomId;

    private Long fromUserId;

}
