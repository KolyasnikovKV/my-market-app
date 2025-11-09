package yandex.practicum.market.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yandex.practicum.market.entity.SessionEntity;

import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, Long> {
    Optional<SessionEntity> findBySessionId(String sessionId);
}
