package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Collection<Booking> findAllByBooker_IdOrderByStartDesc(Long id);

    Collection<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(Long id, LocalDateTime end);

    Collection<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(Long id, LocalDateTime start);

    Collection<Booking> findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long id,
                                                                                    LocalDateTime start, LocalDateTime end);

    Collection<Booking> findAllByBooker_IdAndBookingStatusOrderByStartDesc(Long id, BookingStatus bookingStatus);

    @Query(value = "select * from bookings b join items i on b.item_id=i.item_id" +
            " where owner_id = ? order by start_date desc", nativeQuery = true)
    Collection<Booking> findAllByOwnerIdOrderByStartDesc(Long id);

    @Query(value = "select * from bookings b join items i on b.item_id=i.item_id " +
            "where owner_id = ? and end_date < ? order by start_date desc", nativeQuery = true)
    Collection<Booking> findAllByOwnerIdAndEndBeforeOrderByStartDesc(Long id, LocalDateTime end);

    @Query(value = "select * from bookings b join items i on b.item_id=i.item_id " +
            "where owner_id = ? and start_date > ? order by start_date desc", nativeQuery = true)
    Collection<Booking> findAllByOwnerIdAndStartAfterOrderByStartDesc(Long id, LocalDateTime start);

    @Query(value = "select * from bookings b join items i on b.item_id=i.item_id " +
            "where owner_id = ? and start_date < ? and end_date > ? order by start_date desc", nativeQuery = true)
    Collection<Booking> findAllByOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long id,
                                                                                  LocalDateTime start, LocalDateTime end);

    @Query(value = "select * from bookings b join items i on b.item_id=i.item_id  " +
            "where owner_id = ? and STATUS like ? order by start_date desc", nativeQuery = true)
    Collection<Booking> findAllByOwnerIdAndBookingStatusOrderByStartDesc(Long id, String bookingStatus);

    @Query("select new ru.practicum.shareit.booking.dto.BookingDtoForItem(b.id, u.id) from Booking b" +
            " join b.booker u where b.item.id = ?1 and b.bookingStatus = ?2 order by b.start asc")
    List<BookingDtoForItem> findAllByItem_IdAndStatusOrderByStartAsc(Long id, BookingStatus status);

    List<Booking> findAllByBooker_IdAndItem_IdAndEndBeforeOrderByStartDesc(Long userId, Long itemId, LocalDateTime end);

}
