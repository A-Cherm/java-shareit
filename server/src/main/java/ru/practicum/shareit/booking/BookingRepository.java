package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByUserIdOrderByStartDesc(long userId);

    @Query("""
            select b
            from Booking as b
            where b.user.id = ?1
            and (?2 between b.start and b.end)
            order by b.start desc
            """)
    List<Booking> findCurrentByUserId(long userId, LocalDateTime date);

    List<Booking> findAllByUserIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime date);

    List<Booking> findAllByUserIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime date);

    List<Booking> findAllByUserIdAndStatusOrderByStartDesc(long userId, BookingStatus status);

    List<Booking> findAllByItemUserIdOrderByStartDesc(long userId);

    @Query("""
            select b
            from Booking as b
            where b.item.user.id = ?1
            and (?2 between b.start and b.end)
            order by b.start desc
            """)
    List<Booking> findCurrentByOwnerId(long userId, LocalDateTime date);

    List<Booking> findAllByItemUserIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime date);

    List<Booking> findAllByItemUserIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime date);

    List<Booking> findAllByItemUserIdAndStatusOrderByStartDesc(long userId, BookingStatus status);

    List<Booking> findAllByItemId(long itemId);

    boolean existsByUserIdAndItemIdAndStatusAndEndBefore(long userId, long itemId,
                                                         BookingStatus status, LocalDateTime date);
}
