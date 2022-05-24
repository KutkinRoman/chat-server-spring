package chat.controller;

import chat.dto.UserDto;
import chat.dto.UserMapper;
import chat.model.User;
import chat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class ChatUserController extends ExceptionHandlerController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @GetMapping("{userId}")
    public ResponseEntity<?> getUserInfo (@PathVariable Long userId) {
        User user = userService.getUserByIdOrTrow (userId);
        UserDto userDto = userMapper.map (user);
        return ResponseEntity.ok (userDto);
    }
}
