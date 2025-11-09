package yandex.practicum.market.repository;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import yandex.practicum.market.entity.SessionEntity;
import yandex.practicum.market.entity.ItemEntity;
import yandex.practicum.market.entity.OrderEntity;
import yandex.practicum.market.entity.OrderItemEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class OrderItemRepositoryTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @Transactional
    void sumTotalCostInOrder_shouldCalculateCorrectTotal() {
        // Arrange
        SessionEntity cart = new SessionEntity("1");
        SessionEntity savedCart = entityManager.persist(cart);

        ItemEntity item1 = new ItemEntity("title1", "desc1", "img1.jpg", BigDecimal.ONE);
        ItemEntity item2 = new ItemEntity("title2", "desc2", "img2.jpg", BigDecimal.TWO);
        entityManager.persist(item1);
        entityManager.persist(item2);

        OrderEntity order = new OrderEntity(savedCart);

        entityManager.persist(order.getSession());

        entityManager.persist(order);

        OrderItemEntity orderDetail1 = new OrderItemEntity(order, item1, 2, item1.getPrice()); // 1 * 2 = 2
        OrderItemEntity orderDetail2 = new OrderItemEntity(order, item2, 3, item2.getPrice()); // 2 * 3 = 6
        entityManager.persist(orderDetail1);
        entityManager.persist(orderDetail2);
        entityManager.flush();

        entityManager.persist(order);

        // Act
        Optional<BigDecimal> result = orderItemRepository.sumTotalCostInOrder(order.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("8.00"), result.get());
    }

    @Test
    @Transactional
    void sumTotalCostInOrder_shouldReturnEmptyForEmptyOrder() {
        // Arrange
        OrderEntity order = new OrderEntity(new SessionEntity("1"));
        entityManager.persist(order.getSession());
        entityManager.persist(order);
        entityManager.flush();

        // Act
        Optional<BigDecimal> result = orderItemRepository.sumTotalCostInOrder(order.getId());

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @Transactional
    void shouldPersistOrderDetailWithCompositeKey() {
        // Arrange
        OrderEntity order = new OrderEntity(new SessionEntity("session1"));
        ItemEntity item = new ItemEntity("title1", "desc1", "img1.jpg", BigDecimal.ONE);

        entityManager.persist(order.getSession());
        entityManager.persist(item);
        entityManager.persist(order);

        OrderItemEntity detail = new OrderItemEntity(order, item, 1, item.getPrice());

        // Act
        OrderItemEntity savedDetail = orderItemRepository.save(detail);
        entityManager.flush();

        // Assert
        assertNotNull(savedDetail);
        assertEquals(order.getId(), savedDetail.getOrder().getId());
        assertEquals(item.getId(), savedDetail.getItem().getId());
    }

    @Test
    void shouldNotAllowNegativeQuantity() {
        // Arrange
        OrderEntity order = new OrderEntity(new SessionEntity("session1"));
        ItemEntity item = new ItemEntity("title1", "desc1", "img1.jpg", BigDecimal.ONE);

        entityManager.persist(order.getSession());
        entityManager.persist(item);
        entityManager.persist(order);

        OrderItemEntity detail = new OrderItemEntity(order, item, -1, item.getPrice());

        // Act & Assert
        assertThrows(Exception.class, () -> {
            orderItemRepository.saveAndFlush(detail);
        });
    }

    @Test
    void shouldNotAllowNegativePrice() {
        // Arrange
        OrderEntity order = new OrderEntity(new SessionEntity("session1"));
        ItemEntity item = new ItemEntity("title1", "desc1", "img1.jpg", BigDecimal.ONE);

        entityManager.persist(order.getSession());
        entityManager.persist(item);
        entityManager.persist(order);

        OrderItemEntity detail = new OrderItemEntity(order, item, 1, new BigDecimal("-5.00"));

        // Act & Assert
        assertThrows(Exception.class, () -> {
            orderItemRepository.saveAndFlush(detail);
        });
    }
}
