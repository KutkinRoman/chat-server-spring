package chat.service;

import chat.dto.ChatMessageDto;
import chat.dto.ChatMessagesMapper;
import chat.model.ChatMessage;
import chat.model.ChatRoom;
import chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class ChatMessagePaginationServiceImpl implements ChatMessagePaginationService {

    private final static Integer PAGE_SIZE = 25;
    private final static Integer MAX_PAGE_SIZE = 100;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessagesMapper chatMessagesMapper;

    @Override
    public List<ChatMessageDto> getChatMessagesByRoom (ChatRoom chatRoom, Long lessMessageId, Integer limit) {
        Integer pageSize = validationLimit (limit);
        PageRequest pageRequest = PageRequest.of (0, pageSize, Sort.by ("id").descending ());
        Specification<ChatMessage> spec = Specification.where (null);
        if (lessMessageId != null) {
            spec = spec.and ((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.lessThan (root.get ("id"), lessMessageId));
        }
        spec = spec.and ((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal (root.get ("room").get ("id"), chatRoom.getId ()));
        return chatMessageRepository
                .findAll (spec, pageRequest)
                .stream ()
                .map (chatMessagesMapper::map)
                .collect (Collectors.toList ());
    }

    private Integer validationLimit (Integer limit) {
        return limit == null || limit < 0
                    ? PAGE_SIZE
                    : limit < MAX_PAGE_SIZE
                        ? limit
                        : MAX_PAGE_SIZE;
    }
}
