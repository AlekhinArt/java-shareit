package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByBooker_Id(Long id, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndEndBefore(Long id, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndStartAfter(Long id, LocalDateTime start, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndStartBeforeAndEndAfter(Long id, LocalDateTime start,
                                                              LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndBookingStatus(Long id, BookingStatus status,
                                                     Pageable pageable);

    @Query("select b from Booking b join Item i on b.item.id = i.id where i.ownerId = ?1 order by b.start desc")
    Page<Booking> findAllByOwnerId(Long id, Pageable pageable);

    @Query("select b from Booking b join Item i on b.item.id = i.id where i.ownerId = ?1 " +
            "and b.end < ?2 order by b.start desc")
    Page<Booking> findAllByOwnerIdAndEndBefore(Long id, LocalDateTime end, Pageable pageable);

    @Query("select b from Booking b join Item i on b.item.id = i.id where i.ownerId = ?1 " +
            "and b.start > ?2 order by b.start desc")
    Page<Booking> findAllByOwnerIdAndStartAfter(Long id, LocalDateTime start, Pageable pageable);

    @Query("select b from Booking b join Item i on b.item.id = i.id where i.ownerId = ?1 " +
            "and b.start < ?2 and b.end > ?3 order by b.start desc")
    Page<Booking> findAllByOwnerIdAndStartBeforeAndEndAfter(Long id, LocalDateTime start,
                                                            LocalDateTime end, Pageable pageable);

    @Query("select b from Booking b join Item i on b.item.id = i.id where i.ownerId = ?1 " +
            "and b.bookingStatus = ?2 order by b.start desc")
    Page<Booking> findAllByOwnerIdAndBookingStatus(Long id, BookingStatus bookingStatus, Pageable pageable);

    @Query("select new ru.practicum.shareit.booking.dto.BookingDtoForItem(b.id, u.id) from Booking b" +
            " join b.booker u where b.item.id = ?1 and b.bookingStatus = ?2 order by b.start asc")
    List<BookingDtoForItem> findAllByItem_IdAndStatusOrderByStartAsc(Long id, BookingStatus status);

    List<Booking> findAllByBooker_IdAndItem_IdAndEndBeforeOrderByStartDesc(Long userId, Long itemId, LocalDateTime end);

}
