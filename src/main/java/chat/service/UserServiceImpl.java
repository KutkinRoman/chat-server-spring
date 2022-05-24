package chat.service;

import chat.model.User;
import chat.repository.ChatUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
class UserServiceImpl implements UserService {

    private final ChatUserRepository chatUserRepository;
    private final WebClient webClient;

    @Override
    public User getUserByIdOrTrow (Long userId) {
        Optional<User> chatUser = chatUserRepository.findById (userId);

        if (chatUser.isPresent ()) {
            return chatUser.get ();

        } else if (existsById (userId)) {
            return chatUserRepository.save (new User (userId));
        }

        throw new EntityNotFoundException (String.format ("User not fount by id[%s]", userId));
    }

    @Override
    public void save (User user) {
        chatUserRepository.save (user);
    }

    @Override
    public boolean existsById (Long userId) {
        return Boolean.TRUE.equals (webClient
                .get ()
                .uri ("/api/user/exists/" + userId)
                .retrieve ()
                .bodyToMono (Boolean.class)
                .doOnError (error -> log.error ("An error has occurred {}", error.getMessage ()))
                .onErrorResume (error -> Mono.just (false))
                .block ());
    }
}
