package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.request.CreateItemRequestDto;
import ru.practicum.request.ItemRequestDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemShortDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createRequest(Long userId, CreateItemRequestDto createItemRequestDto) {
        User user = userService.getEntityById(userId);

        ItemRequest request = new ItemRequest();
        request.setDescription(createItemRequestDto.getDescription());
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());

        request = itemRequestRepository.save(request);

        return ItemRequestMapper.toDto(request);
    }

    @Override
    public List<ItemRequestResponseDto> getOwnRequests(Long userId) {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        if (requests.isEmpty()) return List.of();

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        List<Item> items = itemRepository.findAllByRequestIdIn(requestIds);

        Map<Long, List<ItemShortDto>> itemsByRequest = items.stream()
                .collect(Collectors.groupingBy(
                        Item::getRequestId,
                        Collectors.mapping(
                                item -> ItemShortDto.builder()
                                        .id(item.getId())
                                        .name(item.getName())
                                        .ownerId(item.getOwner().getId())
                                        .build(),
                                Collectors.toList()
                        )
                ));

        return requests.stream()
                .map(req -> ItemRequestResponseDto.builder()
                        .id(req.getId())
                        .description(req.getDescription())
                        .created(req.getCreated())
                        .items(itemsByRequest.getOrDefault(req.getId(), List.of()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        return itemRequestRepository.findAllByRequesterIdNot(userId).stream()
                .map(ItemRequestMapper::toDto)
                .toList();
    }

    @Override
    public ItemRequestResponseDto getRequestById(Long userId, Long requestId) {
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        List<Item> items = itemRepository.findAllByRequestId(requestId);

        List<ItemShortDto> itemDtos = items.stream()
                .map(item -> ItemShortDto.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .ownerId(item.getOwner().getId())
                        .requestId(item.getRequestId()) // если поле нужно
                        .build()
                )
                .toList();

        return ItemRequestResponseDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(itemDtos)
                .build();
    }
}