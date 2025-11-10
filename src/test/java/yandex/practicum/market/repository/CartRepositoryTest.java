package yandex.practicum.market.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import yandex.practicum.market.entity.CartEntity;
import yandex.practicum.market.entity.CartItemEntity;
import yandex.practicum.market.entity.ItemEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CartRepositoryTest {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @Transactional
    void findBySessionId_shouldReturnSessionWhenExists() {
        // Arrange
        String sessionId = "1";
        CartEntity cartEntity = new CartEntity(sessionId);
        cartRepository.save(cartEntity);

        // Act
        Optional<CartEntity> foundSessionEntity = cartRepository.findBySessionId(sessionId);

        // Assert
        assertTrue(foundSessionEntity.isPresent());
        assertEquals(sessionId, foundSessionEntity.get().getSessionId());
    }

    @Test
    void findBySessionId_shouldReturnEmptyWhenNotExists() {
        // Act
        Optional<CartEntity> foundSession = cartRepository.findBySessionId("0");

        // Assert
        assertFalse(foundSession.isPresent());
    }

    @Test
    @Transactional
    void shouldSaveSessionWithSessionId() {
        // Arrange
        String sessionId = "1";
        CartEntity newCartEntity = new CartEntity(sessionId);

        // Act
        CartEntity savedCartEntity = cartRepository.save(newCartEntity);

        // Assert
        assertNotNull(savedCartEntity.getId());
        assertEquals(sessionId, savedCartEntity.getSessionId());
    }

    @Test
    @Transactional
    void shouldGenerateIdAutomatically() {
        // Arrange
        CartEntity cartEntity = new CartEntity("1");

        // Act
        CartEntity savedCartEntity1 = cartRepository.save(cartEntity);

        // Assert
        assertNotNull(savedCartEntity1.getId());
    }

    @Test
    void shouldNotAllowNullSessionId() {
        // Arrange
        CartEntity sessionWithNullSessionId = new CartEntity();

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> {
            cartRepository.saveAndFlush(sessionWithNullSessionId);
        });
    }

    @Test
    @Transactional
    void shouldNotAllowDuplicateSessionIds() {
        // Arrange
        String duplicateSessionId = "1";
        cartRepository.save(new CartEntity(duplicateSessionId));

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> {
            cartRepository.save(new CartEntity(duplicateSessionId));
        });
    }

    @Test
    @Transactional
    void shouldPersistSessionWithDetails() {
        // Arrange
        ItemEntity item = new ItemEntity("title1", "desc1", "img1.jpg", BigDecimal.ONE);
        entityManager.persist(item);

        CartEntity cartEntity = new CartEntity("1");
        CartItemEntity detail = new CartItemEntity(cartEntity, item, 1, BigDecimal.ONE);
        cartEntity.getItems().put(item, detail);

        // Act
        CartEntity savedCartEntity = cartRepository.save(cartEntity);

        // Assert
        assertNotNull(savedCartEntity.getId());
        assertEquals(1, savedCartEntity.getItems().size());
    }

}
