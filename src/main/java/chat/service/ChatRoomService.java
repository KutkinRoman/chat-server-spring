package chat.service;

import chat.dto.ChatRoomDto;
import chat.model.ChatRoom;
import chat.model.User;

import java.util.List;
import java.util.Optional;

public interface ChatRoomService {

    ChatRoomDto getChatRoomByUsers (User authUser, User user);

    List<ChatRoomDto> getCatRoomsByUser (User authUser);

    Optional<ChatRoom> findChatRoomByIdAndByUser (Long chatRoomId, User authUser);

    Optional<ChatRoom> findChatRoomByIdAndByUsers (Long chatRoomId, User authUser, User user);

    void updateEventDataTimeByChatRoom (ChatRoom chatRoom);

    Long getTotalMessagesByChatRoom (ChatRoom chatRoom);

    Long getTotalNewMessageByUser (User user);

    Long getTotalNewMessageByUserAndByChatRoom (User user, ChatRoom chatRoom);
}
