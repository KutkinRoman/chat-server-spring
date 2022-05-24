package chat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @OneToOne
    private User fromUser;

    @OneToOne
    private User toUser;

    private LocalDateTime createDateTime;

    @Column(length = 1500)
    private String text;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne()
    @JoinColumn()
    private ChatRoom room;

    private transient String tmpId;

    public enum Status {
        SEND,
        RECEIVED,
        READ,
        DELETE
    }

}
