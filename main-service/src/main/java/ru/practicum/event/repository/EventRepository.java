package ru.practicum.event.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("select e from Event e where e.user.id = :userId")
    Page<Event> findAllByUserId(Long userId, Pageable pageable);

    List<Event> findAllByIdIn(List<Long> events);

    Optional<Event> findByIdAndState(Long id, EventState state);

    Optional<Event> findByIdAndUser_Id(Long eventId, Long userId);

    Optional<List<Event>> findAllByCategoryId(Long id);

    @Query("""
                SELECT e
                FROM Event e
                WHERE (:users IS NULL OR e.user.id IN :users)
                  AND (:states IS NULL OR e.state IN :states)
                  AND (:categories IS NULL OR e.category.id IN :categories)
                  AND (:rangeStart IS NULL OR e.eventDate >= :rangeStart)
                  AND (:rangeEnd IS NULL OR e.eventDate <= :rangeEnd)
            """)
    Page<Event> findAllEvents(List<Long> users, List<String> states, List<Long> categories,
                              LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

}
