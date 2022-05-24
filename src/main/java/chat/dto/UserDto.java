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
public class UserDto {

    private Long id;

    private LocalDateTime dateOnline;

    private Status status;

    public enum Status {
        ONLINE,
        OFFLINE;
    }
}
