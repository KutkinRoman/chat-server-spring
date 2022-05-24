package chat.dto;


import chat.dto.UserDto.Status;
import chat.dto.interf.EntityDtoMapper;
import chat.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;

import static chat.dto.UserDto.Status.OFFLINE;
import static chat.dto.UserDto.Status.ONLINE;

@Component
@RequiredArgsConstructor
public class UserMapper implements EntityDtoMapper<User, UserDto> {

    private final SimpUserRegistry simpUserRegistry;

    @Override
    public UserDto map (User entity) {
        final String id = entity.getId ().toString ();
        final SimpUser simpUser = simpUserRegistry.getUser (id);
        Status status = simpUser != null ? ONLINE : OFFLINE;
        return UserDto.builder ()
                .id (entity.getId ())
                .dateOnline (entity.getDateOnline ())
                .status (status)
                .build ();
    }
}
