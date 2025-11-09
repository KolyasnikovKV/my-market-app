package yandex.practicum.market.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import yandex.practicum.market.entity.SessionEntity;
import yandex.practicum.market.entity.CartItemEntity;
import yandex.practicum.market.entity.ItemEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class SessionRepositoryTest {
    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @Transactional
    void findBySessionId_shouldReturnSessionWhenExists() {
        // Arrange
        String sessionId = "1";
        SessionEntity sessionEntity = new SessionEntity(sessionId);
        sessionRepository.save(sessionEntity);

        // Act
        Optional<SessionEntity> foundSessionEntity = sessionRepository.findBySessionId(sessionId);

        // Assert
        assertTrue(foundSessionEntity.isPresent());
        assertEquals(sessionId, foundSessionEntity.get().getSessionId());
    }

    @Test
    void findBySessionId_shouldReturnEmptyWhenNotExists() {
        // Act
        Optional<SessionEntity> foundSession = sessionRepository.findBySessionId("0");

        // Assert
        assertFalse(foundSession.isPresent());
    }

    @Test
    @Transactional
    void shouldSaveSessionWithSessionId() {
        // Arrange
        String sessionId = "1";
        SessionEntity newSessionEntity = new SessionEntity(sessionId);

        // Act
        SessionEntity savedSessionEntity = sessionRepository.save(newSessionEntity);

        // Assert
        assertNotNull(savedSessionEntity.getId());
        assertEquals(sessionId, savedSessionEntity.getSessionId());
    }

    @Test
    @Transactional
    void shouldGenerateIdAutomatically() {
        // Arrange
        SessionEntity sessionEntity = new SessionEntity("1");

        // Act
        SessionEntity savedSessionEntity1 = sessionRepository.save(sessionEntity);

        // Assert
        assertNotNull(savedSessionEntity1.getId());
    }

    @Test
    void shouldNotAllowNullSessionId() {
        // Arrange
        SessionEntity sessionWithNullSessionId = new SessionEntity();

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> {
            sessionRepository.saveAndFlush(sessionWithNullSessionId);
        });
    }

    @Test
    @Transactional
    void shouldNotAllowDuplicateSessionIds() {
        // Arrange
        String duplicateSessionId = "1";
        sessionRepository.save(new SessionEntity(duplicateSessionId));

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> {
            sessionRepository.save(new SessionEntity(duplicateSessionId));
        });
    }

    @Test
    @Transactional
    void shouldPersistSessionWithDetails() {
        // Arrange
        ItemEntity item = new ItemEntity("title1", "desc1", "img1.jpg", BigDecimal.ONE);
        entityManager.persist(item);

        SessionEntity sessionEntity = new SessionEntity("1");
        CartItemEntity detail = new CartItemEntity(sessionEntity, item, 1, BigDecimal.ONE);
        sessionEntity.getItems().put(item, detail);

        // Act
        SessionEntity savedSessionEntity = sessionRepository.save(sessionEntity);

        // Assert
        assertNotNull(savedSessionEntity.getId());
        assertEquals(1, savedSessionEntity.getItems().size());
    }

}
