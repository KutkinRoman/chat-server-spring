package chat.controller;

import chat.dto.ChatMessageDto;
import chat.dto.ChatRoomDto;
import chat.model.ChatRoom;
import chat.model.User;
import chat.service.ChatMessagePaginationService;
import chat.service.ChatRoomService;
import chat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class ChatRoomController extends ExceptionHandlerController {

    private final UserService userService;
    private final ChatRoomService chatRoomService;
    private final ChatMessagePaginationService chatMessagePaginationService;

    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @GetMapping()
    public ResponseEntity<?> getChatRooms (Authentication authentication) {
        User authUser = userService.getUserByIdOrTrow (Long.parseLong (authentication.getName ()));
        List<ChatRoomDto> chatRoomsDto = chatRoomService.getCatRoomsByUser (authUser);
        return ResponseEntity.ok (chatRoomsDto);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<?> getMessagesByChatRoomId (
            @PathVariable Long roomId,
            @RequestParam(required = false) Long lessMessageId,
            @RequestParam(required = false) Integer limit,
            Authentication authentication) {
        User authUser = userService.getUserByIdOrTrow (Long.parseLong (authentication.getName ()));
        ChatRoom chatRoom = chatRoomService.findChatRoomByIdAndByUser (roomId, authUser)
                .orElseThrow (() -> new EntityNotFoundException (
                        String.format ("Room by id[%s] not found", roomId)));
        List<ChatMessageDto> list = chatMessagePaginationService.getChatMessagesByRoom (chatRoom, lessMessageId, limit);
        return ResponseEntity.ok (list);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @PostMapping("/create/{userId}")
    public ResponseEntity<?> createChatRoom (@PathVariable Long userId, Authentication authentication) {
        User authUser = userService.getUserByIdOrTrow (Long.parseLong (authentication.getName ()));
        User user = userService.getUserByIdOrTrow (userId);
        ChatRoomDto chatRoomDto = chatRoomService.getChatRoomByUsers (authUser, user);
        return ResponseEntity.ok (chatRoomDto);
    }
}
