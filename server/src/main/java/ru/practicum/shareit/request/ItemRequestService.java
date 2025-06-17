package ru.practicum.shareit.request;

import ru.practicum.request.CreateItemRequestDto;
import ru.practicum.request.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createRequest(Long userId, CreateItemRequestDto createItemRequestDto);

    List<ItemRequestResponseDto> getOwnRequests(Long userId);

    List<ItemRequestDto> getAllRequests(Long userId);

    ItemRequestResponseDto getRequestById(Long userId, Long requestId);
}