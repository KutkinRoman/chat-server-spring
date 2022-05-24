package chat.service;

import chat.dto.ChatMessageDto;
import chat.dto.ChatMessagesMapper;
import chat.model.ChatMessage;
import chat.model.ChatRoom;
import chat.model.User;
import chat.payload.request.NewChatMessageRequest;
import chat.payload.response.ChatEvent;
import chat.repository.ChatMessageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;

import static chat.model.ChatMessage.Status.*;
import static chat.payload.response.ChatEvent.Type.*;


@Service
@AllArgsConstructor
@Slf4j
class ChatMessageServiceImpl implements ChatMessageService {

    private final UserService userService;
    private final ChatRoomService chatRoomService;
    private final ChatMessagesMapper chatMessagesMapper;
    private final ChatMessageRepository chatMessageRepository;
    private final MessageSender messageSender;

    @Override
    public void handleNewMessage (NewChatMessageRequest newChatMessageRequest) {
        log.info ("Handle new message: {}", newChatMessageRequest);
        ChatMessage message = createNewChatMessage (newChatMessageRequest);
        sendNewMessagesToUser (message, message.getFromUser ());
        sendNewMessagesToUser (message, message.getToUser ());
    }

    @Override
    public void handleMessageReceived (Long userId, Long messageId) {
        log.info ("Handle message received message_id: {}, user_id {}", messageId, userId);
        User user = userService.getUserByIdOrTrow (userId);
        chatMessageRepository.findByIdAndRoomUsersContains (messageId, user)
                .ifPresent (chatMessage -> {
                    if (chatMessage.getStatus ().equals (SEND)) {
                        handleMessageEvent (chatMessage, RECEIVED, RECEIVED_MESSAGE);
                    }
                });
    }

    @Override
    public void handleMessageRead (Long userId, Long messageId) {
        log.info ("Handle message read message_id: {}, user_id {}", messageId, userId);
        User user = userService.getUserByIdOrTrow (userId);
        chatMessageRepository.findByIdAndRoomUsersContains (messageId, user)
                .ifPresent (chatMessage -> {
                    if (!chatMessage.getStatus ().equals (READ)) {
                        handleMessageEvent (chatMessage, READ, READ_MESSAGE);
                    }
                });
    }

    @Override
    public void handleMessageDelete (Long userId, Long messageId) {
        log.info ("Handle message delete message_id: {}, user_id {}", messageId, userId);
        User user = userService.getUserByIdOrTrow (userId);
        chatMessageRepository.findByIdAndFromUser (messageId, user)
                .ifPresent (chatMessage -> {
                    if (!chatMessage.getStatus ().equals (DELETE)) {
                        handleMessageEvent (chatMessage, DELETE, DELETE_MESSAGE);
                    }
                });
    }

    private ChatMessage createNewChatMessage (NewChatMessageRequest newChatMessageRequest) {
        Long fromUserId = newChatMessageRequest.getFromUserId ();
        Long toUserId = newChatMessageRequest.getToUserId ();
        Long roomId = newChatMessageRequest.getRoomId ();
        User fromUser = userService.getUserByIdOrTrow (fromUserId);
        User toUser = userService.getUserByIdOrTrow (toUserId);
        ChatRoom chatRoom = chatRoomService
                .findChatRoomByIdAndByUsers (roomId, fromUser, toUser)
                .orElseThrow (() -> new EntityNotFoundException (
                        String.format ("Room by id[%s] not found", roomId)
                ));
        ChatMessage chatMessage = ChatMessage
                .builder ()
                .tmpId (newChatMessageRequest.getTmpId ())
                .text (newChatMessageRequest.getText ())
                .createDateTime (LocalDateTime.now ())
                .status (SEND)
                .fromUser (fromUser)
                .toUser (toUser)
                .room (chatRoom)
                .build ();
        chatRoomService.updateEventDataTimeByChatRoom (chatRoom);
        return chatMessageRepository.save (chatMessage);
    }

    public void handleMessageEvent (ChatMessage chatMessage, ChatMessage.Status status, ChatEvent.Type type) {
        chatMessage.setStatus (status);
        if (status.equals (DELETE)) {
            chatMessageRepository.delete (chatMessage);
        } else {
            chatMessageRepository.save (chatMessage);
        }
        ChatEvent chatEvent = createMessageEvent (chatMessage, type, chatMessage.getFromUser ());
        messageSender.sendChatEventToUser (chatMessage.getFromUser (), chatEvent);
        chatEvent = createMessageEvent (chatMessage, type, chatMessage.getToUser ());
        messageSender.sendChatEventToUser (chatMessage.getToUser (), chatEvent);
    }

    private ChatEvent createMessageEvent (ChatMessage chatMessage, ChatEvent.Type type, User user) {
        HashMap<String, Long> payload = new HashMap<> ();
        payload.put ("messageId", chatMessage.getId ());
        payload.put ("roomId", chatMessage.getRoom ().getId ());
        payload.put ("totalMessage", chatRoomService.getTotalMessagesByChatRoom (chatMessage.getRoom ()));
        payload.put ("totalNewMessage", chatRoomService.getTotalNewMessageByUserAndByChatRoom (user, chatMessage.getRoom ()));
        payload.put ("totalNewMessagesUser", chatRoomService.getTotalNewMessageByUser (user));
        return new ChatEvent (type, payload);
    }

    private void sendNewMessagesToUser (ChatMessage chatMessage, User user) {
        ChatMessageDto chatMessageDto = chatMessagesMapper.map (chatMessage);
        HashMap<String, Object> payload = new HashMap<> ();
        payload.put ("totalMessages", chatRoomService.getTotalMessagesByChatRoom (chatMessage.getRoom ()));
        payload.put ("totalNewMessages", chatRoomService.getTotalNewMessageByUserAndByChatRoom (user, chatMessage.getRoom ()));
        payload.put ("totalNewMessagesUser", chatRoomService.getTotalNewMessageByUser (user));
        payload.put ("message", chatMessageDto);
        ChatEvent chatEvent = new ChatEvent (NEW_MESSAGE, payload);
        messageSender.sendChatEventToUser (user, chatEvent);
    }

}