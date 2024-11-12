package ru.practicum.stat.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.stat.server.model.EndpointHitEntity;
import ru.practicum.stat.server.projection.ViewStatsProjection;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EndpointHitEntityRepository extends JpaRepository<EndpointHitEntity, Long> {

    @Query("SELECT s.app AS app, s.uri AS uri, COUNT(s) AS hits " +
            "FROM EndpointHitEntity s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "GROUP BY app, uri " +
            "ORDER BY hits DESC")
    List<ViewStatsProjection> getStatsWithHits(@Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end);

    @Query("SELECT s.app AS app, s.uri AS uri, COUNT(s) AS hits " +
            "FROM EndpointHitEntity s " +
            "WHERE s.timestamp BETWEEN :start AND :end AND s.uri IN :uris " +
            "GROUP BY app, uri " +
            "ORDER BY hits DESC")
    List<ViewStatsProjection> getStatsWithHitsAndUris(@Param("start") LocalDateTime start,
                                                      @Param("end") LocalDateTime end,
                                                      @Param("uris") List<String> uris);

    @Query("SELECT s.app AS app, s.uri AS uri, COUNT(DISTINCT s.ip) AS hits " +
            "FROM EndpointHitEntity s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "GROUP BY app, uri " +
            "ORDER BY hits DESC")
    List<ViewStatsProjection> getUniqueStatsWithHits(@Param("start") LocalDateTime start,
                                                     @Param("end") LocalDateTime end);

    @Query("SELECT s.app AS app, s.uri AS uri, COUNT(DISTINCT s.ip) AS hits " +
            "FROM EndpointHitEntity s " +
            "WHERE s.timestamp BETWEEN :start AND :end AND s.uri IN :uris " +
            "GROUP BY app, uri " +
            "ORDER BY hits DESC")
    List<ViewStatsProjection> getUniqueStatsWithHitsAndUris(@Param("start") LocalDateTime start,
                                                            @Param("end") LocalDateTime end,
                                                            @Param("uris") List<String> uris);
}