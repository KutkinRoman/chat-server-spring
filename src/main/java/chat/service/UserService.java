package chat.service;

import chat.model.ChatRoom;
import chat.model.User;

public interface UserService {

    User getUserByIdOrTrow(Long userId);

    void save(User user);

    boolean existsById(Long userId);

}
