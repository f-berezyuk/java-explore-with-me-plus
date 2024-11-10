package ru.practicum.stat.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.stat.server.model.EndpointHitEntity;

public interface EndpointHitEntityRepository extends JpaRepository<EndpointHitEntity, Long> {
}