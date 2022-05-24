package chat.service;

import chat.model.User;
import chat.payload.response.ChatEvent;
import chat.repository.ChatMessageRepository;
import chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;

import static chat.payload.response.ChatEvent.Type.USER_OFFLINE;
import static chat.payload.response.ChatEvent.Type.USER_ONLINE;

@Service
@Slf4j
@RequiredArgsConstructor
class UserEventServiceImpl implements UserEventService {

    private final UserService userService;
    private final ChatMessageRepository chatMessageRepository;
    private final MessageSender messageSender;
    private final ChatRoomRepository chatRoomRepository;
    private final SimpUserRegistry simpUserRegistry;

    @Override
    @EventListener
    @Transactional
    public void handleSessionConnected (SessionConnectEvent event) {
        User user = userService.getUserByIdOrTrow (Long.parseLong (Objects.requireNonNull (event.getUser ()).getName ()));
        updateDataOnline (user);
        sendUserEventToUsers (user, USER_ONLINE);
        log.info ("User connected USER_ID: {}", user.getId ());
    }

    @Override
    @EventListener
    @Transactional
    public void handleSessionDisconnect (SessionDisconnectEvent event) {
        User user = userService.getUserByIdOrTrow (Long.parseLong (Objects.requireNonNull (event.getUser ()).getName ()));
        if (simpUserRegistry.getUser (event.getUser ().getName ()) == null) {
            updateDataOnline (user);
            sendUserEventToUsers (user, USER_OFFLINE);
        }
        log.info ("User Disconnect USER_ID: {}", user.getId ());
    }

    private void updateDataOnline (User user) {
        user.setDateOnline (LocalDateTime.now ());
        userService.save (user);
    }

    private void sendUserEventToUsers (User user, ChatEvent.Type type) {
        chatRoomRepository
                .findAllByUsersContains (user)
                .forEach (chatRoom -> {
                    chatRoom
                            .getUsers ()
                            .forEach (toUser -> {
                                if (!toUser.equals (user)) {
                                    ChatEvent chatEvent = createUserEvent (user.getId (), chatRoom.getId (), type);
                                    messageSender.sendChatEventToUser (toUser, chatEvent);
                                }
                            });
                });
    }

    private ChatEvent createUserEvent (Long userId, Long chatRoomId, ChatEvent.Type type) {
        HashMap<String, Long> payload = new HashMap<> ();
        payload.put ("roomId", chatRoomId);
        payload.put ("userId", userId);
        return new ChatEvent (type, payload);
    }

}
