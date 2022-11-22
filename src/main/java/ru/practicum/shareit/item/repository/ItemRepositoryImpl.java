package ru.practicum.shareit.item.repository;

import org.springframework.context.annotation.Lazy;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;

import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class ItemRepositoryImpl implements ItemRepositoryCustom {
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;

    public ItemRepositoryImpl(BookingRepository bookingRepository, CommentRepository commentRepository,
                              @Lazy ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemDto getByIdForResponse(long userId, long id) {
        ItemDto itemDto = ItemMapper.toItemDto(itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Нет такого предмета")));
        List<Comment> comments = commentRepository.findAllByItem_Id(id);
        if (comments.isEmpty()) {
            itemDto.setComments(Collections.emptyList());
        } else {
            itemDto.setComments(comments.stream().map(CommentMapper::toCommentDtoResponse).collect(Collectors.toList()));
        }
        addBookings(userId, itemDto);
        return itemDto;
    }

    private ItemDto addBookings(Long userId, ItemDto itemDto) {
        if (userId == itemDto.getOwnerId()) {
            List<BookingDtoForItem> bookings = getBooking(itemDto.getId());
            if (bookings.size() > 1) {
                itemDto.setLastBooking(bookings.get(0));
            }
            if (bookings.size() >= 2) {
                itemDto.setNextBooking(bookings.get(1));
            }
        }
        return itemDto;

    }

    private List<BookingDtoForItem> getBooking(Long itemId) {
        return bookingRepository.findAllByItem_IdAndStatusOrderByStartAsc(itemId, BookingStatus.APPROVED);
    }
}
