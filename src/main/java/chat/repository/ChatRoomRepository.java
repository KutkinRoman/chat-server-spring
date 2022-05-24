package chat.repository;

import chat.model.ChatRoom;
import chat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Collection<ChatRoom> findByUsersContainsOrderByLastEventDateTimeDesc (User user);

    Collection<ChatRoom> findAllByUsersContains (User user);

    Optional<ChatRoom> findByUsersContainsAndUsersContains (User user1, User user2);

    Optional<ChatRoom> findByIdAndUsersContains (Long roomId, User user);

    Optional<ChatRoom> findByIdAndUsersContainsAndUsersContains (Long roomId, User user1, User user2);
}
