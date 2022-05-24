package chat.payload.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatEvent {

    private final LocalDateTime dateTime;
    private Type type;
    private Object payload;

    public ChatEvent (Type type, Object payload) {
        this.dateTime = LocalDateTime.now ();
        this.type = type;
        this.payload = payload;
    }

    public enum Type {
        NEW_MESSAGE,
        RECEIVED_MESSAGE,
        READ_MESSAGE,
        DELETE_MESSAGE,
        USER_OFFLINE,
        USER_ONLINE;
    }
}
