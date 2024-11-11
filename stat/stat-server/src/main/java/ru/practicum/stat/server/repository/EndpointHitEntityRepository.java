package ru.practicum.stat.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.stat.dto.ViewStats;
import ru.practicum.stat.server.model.EndpointHitEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EndpointHitEntityRepository extends JpaRepository<EndpointHitEntity, Long> {

    @Query("SELECT s.app AS app, s.uri AS uri, COUNT(s) AS hits " +
            "FROM EndpointHitEntity s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "GROUP BY app, uri " +
            "ORDER BY hits DESC")
    List<ViewStats> getStatsWithHits(@Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end);

    @Query("SELECT s.app AS app, s.uri AS uri, COUNT(s) AS hits " +
            "FROM EndpointHitEntity s " +
            "WHERE s.timestamp BETWEEN :start AND :end AND s.uri IN :uris " +
            "GROUP BY app, uri " +
            "ORDER BY hits DESC")
    List<ViewStats> getStatsWithHitsAndUris(@Param("start") LocalDateTime start,
                                                      @Param("end") LocalDateTime end,
                                                      @Param("uris") List<String> uris);

    @Query("SELECT s.app AS app, s.uri AS uri, COUNT(DISTINCT s.ip) AS hits " +
            "FROM EndpointHitEntity s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "GROUP BY app, uri " +
            "ORDER BY hits DESC")
    List<ViewStats> getUniqueStatsWithHits(@Param("start") LocalDateTime start,
                                                     @Param("end") LocalDateTime end);

    @Query("SELECT s.app AS app, s.uri AS uri, COUNT(DISTINCT s.ip) AS hits " +
            "FROM EndpointHitEntity s " +
            "WHERE s.timestamp BETWEEN :start AND :end AND s.uri IN :uris " +
            "GROUP BY app, uri " +
            "ORDER BY hits DESC")
    List<ViewStats> getUniqueStatsWithHitsAndUris(@Param("start") LocalDateTime start,
                                                            @Param("end") LocalDateTime end,
                                                            @Param("uris") List<String> uris);
}