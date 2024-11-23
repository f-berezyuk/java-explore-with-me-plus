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
        WHERE e.state = 'PUBLISHED'
          AND (:text IS NULL OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) 
                           OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')))
          AND (:categories IS NULL OR e.category.id IN :categories)
          AND (:paid IS NULL OR e.paid = :paid)
          AND (:rangeStart IS NULL OR e.eventDate >= :rangeStart)
          AND (:rangeEnd IS NULL OR e.eventDate <= :rangeEnd)
          AND (:onlyAvailable IS NULL OR :onlyAvailable = FALSE OR 
               e.participantLimit = 0 OR e.confirmedRequests < e.participantLimit)
    """)
    Page<Event> findPublicEvents(String text, List<Long> categories, Boolean paid,
                                 LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                 Boolean onlyAvailable, Pageable pageable);
}
