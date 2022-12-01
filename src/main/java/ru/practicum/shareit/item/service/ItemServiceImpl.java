package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(UserRepository userRepository, ItemRepository itemRepository,
                           BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemDto addNewItem(long userId, Item item) {
        checkUser(userId);
        item.setOwnerId(userId);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(Item item, long userId, long itemId) {
        Item oldItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Такого предмета нет"));
        if (oldItem.getOwnerId() == null || oldItem.getOwnerId() != userId)
            throw new NotFoundException("Не владелец = нет прав");
        checkUser(userId);
        item.setOwnerId(userId);
        if (item.getName() == null) item.setName(oldItem.getName());
        if (item.getDescription() == null) item.setDescription(oldItem.getDescription());
        if (item.getAvailable() == null) item.setAvailable(oldItem.getAvailable());
        item.setId(oldItem.getId());
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItem(long userId, long itemId) {
        checkUser(userId);
        return itemRepository.getByIdForResponse(userId, itemId);
    }

    @Override
    public Collection<ItemDto> getItemsCreator(long userId, int from, int size) {
        checkUser(userId);
        checkPageParam(from, size);
        Pageable pageable = PageRequest.of(from / size, size);
        return itemRepository.findIdByOwner(userId, pageable).stream().map(id
                -> itemRepository.getByIdForResponse(userId, id)).collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> findItem(String description, long userId, int from, int size) {
        if (description.isBlank() || description.isEmpty()) return new ArrayList<>();
        checkPageParam(from, size);
        //checkUser(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        return ItemMapper.mapToItemDto(itemRepository.search(description, pageable));
    }

    @Override
    public CommentDtoResponse addComment(long userId, long itemId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Нет предмета с таким id"));
        checkUser(userId);
        List<Booking> booking = bookingRepository.findAllByBooker_IdAndItem_IdAndEndBeforeOrderByStartDesc(userId, itemId, LocalDateTime.now());
        if (booking == null || booking.size() == 0) {
            throw new ValidationException("Предмет не забронирован");
        }
        Comment comment = Comment.builder()
                .author(userRepository.findById(userId).get())
                .text(commentDto.getText())
                .item(item)
                .created(LocalDateTime.now())
                .build();
        return CommentMapper.toCommentDtoResponse(commentRepository.save(comment));
    }

    private void checkPageParam(int from, int size) {
        if (from < 0) {
            throw new ValidationException("Индекс первого элемента должен быть больше 0");
        }
        if (size <= 0) {
            throw new ValidationException("Количество предметов должно быть больше 0");
        }
    }

    private void checkUser(long userId) {
        if (userId == 0) throw new ValidationException("Не задан пользователь");
        if (userRepository.findById(userId).isEmpty()) throw new NotFoundException("Пользователь не найден");
    }

}
