package chat.repository;

import chat.model.ChatMessage;
import chat.model.ChatRoom;
import chat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>, JpaSpecificationExecutor<ChatMessage> {

    Long countByRoom (ChatRoom chatRoom);

    Long countByRoomAndStatusNotAndToUser (ChatRoom chatRoom, ChatMessage.Status status, User toUser);

    Long countByStatusNotAndToUser(ChatMessage.Status status, User toUser);

    Optional<ChatMessage> findFirstByRoomOrderByIdDesc (ChatRoom chatRoom);

    Optional<ChatMessage> findByIdAndRoomUsersContains(Long messageId, User user);

    Optional<ChatMessage> findByIdAndFromUser(Long messageId, User user);
}
