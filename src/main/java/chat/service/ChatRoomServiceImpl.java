package chat.service;

import chat.dto.*;
import chat.model.ChatRoom;
import chat.model.User;
import chat.repository.ChatMessageRepository;
import chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static chat.model.ChatMessage.Status.READ;

@Service
@RequiredArgsConstructor
class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserMapper userMapper;
    private final ChatMessagesMapper chatMessagesMapper;

    @Override
    public List<ChatRoomDto> getCatRoomsByUser (User authUser) {
        Long totalNewMessagesUser = getTotalNewMessageByUser (authUser);
        return chatRoomRepository.findByUsersContainsOrderByLastEventDateTimeDesc (authUser).stream ()
                .map (chatRoom -> createChatRoomDto (authUser, chatRoom, totalNewMessagesUser))
                .collect (Collectors.toList ());
    }

    @Override
    public Optional<ChatRoom> findChatRoomByIdAndByUser (Long chatRoomId, User authUser) {
        return chatRoomRepository.findByIdAndUsersContains (chatRoomId, authUser);
    }

    @Transactional
    @Override
    public ChatRoomDto getChatRoomByUsers (User authUser, User user) {
        Long totalNewMessagesUser = getTotalNewMessageByUser (authUser);
        Optional<ChatRoom> result = chatRoomRepository.findByUsersContainsAndUsersContains (authUser, user);
        if (result.isPresent ()) {
            return createChatRoomDto (authUser, result.get (), totalNewMessagesUser);
        }
        return createChatRoomDto (authUser, createNewChatRoom (authUser, user), totalNewMessagesUser);
    }

    @Override
    public Optional<ChatRoom> findChatRoomByIdAndByUsers (Long chatRoomId, User authUser, User user) {
        return chatRoomRepository.findByIdAndUsersContainsAndUsersContains (chatRoomId, authUser, user);
    }

    @Override
    public void updateEventDataTimeByChatRoom (ChatRoom chatRoom) {
        chatRoom.setLastEventDateTime (LocalDateTime.now ());
        chatRoomRepository.save (chatRoom);
    }

    @Override
    public Long getTotalMessagesByChatRoom (ChatRoom chatRoom) {
        return chatMessageRepository.countByRoom (chatRoom);
    }

    @Override
    public Long getTotalNewMessageByUser (User user) {
        return chatMessageRepository.countByStatusNotAndToUser (READ, user);
    }

    @Override
    public Long getTotalNewMessageByUserAndByChatRoom (User user, ChatRoom chatRoom) {
        return chatMessageRepository.countByRoomAndStatusNotAndToUser (chatRoom, READ, user);
    }

    private ChatRoom createNewChatRoom (User authUser, User user) {
        List<User> users = new ArrayList<> ();
        users.add (authUser);
        users.add (user);
        ChatRoom chatRoom = ChatRoom
                .builder ()
                .lastEventDateTime (LocalDateTime.now ())
                .users (users)
                .build ();
        return chatRoomRepository.save (chatRoom);
    }

    private ChatRoomDto createChatRoomDto (User authUser, ChatRoom chatRoom, Long totalNewMessagesUser) {
        UserDto chatUser = chatRoom
                .getUsers ()
                .stream ()
                .filter (u -> !u.getId ().equals (authUser.getId ()))
                .findAny ()
                .map (userMapper::map).orElse (null);
        Long totalMessages = getTotalMessagesByChatRoom (chatRoom);
        Long totalNewMessages = getTotalNewMessageByUserAndByChatRoom (authUser, chatRoom);
        ChatMessageDto chatMessageDto = chatMessageRepository
                .findFirstByRoomOrderByIdDesc (chatRoom)
                .map (chatMessagesMapper::map)
                .orElse (null);
        return ChatRoomDto
                .builder ()
                .id (chatRoom.getId ())
                .chatUser (chatUser)
                .lastEventDateTime (chatRoom.getLastEventDateTime ())
                .lastMessage (chatMessageDto)
                .totalMessages (totalMessages)
                .totalNewMessages (totalNewMessages)
                .totalNewMessagesUser(totalNewMessagesUser)
                .build ();
    }

}
