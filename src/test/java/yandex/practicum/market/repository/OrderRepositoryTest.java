package yandex.practicum.market.repository;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import yandex.practicum.market.entity.SessionEntity;
import yandex.practicum.market.entity.CartItemEntity;
import yandex.practicum.market.entity.ItemEntity;
import yandex.practicum.market.entity.OrderEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @Transactional
    void findByCartId_shouldReturnOrdersForSession() {
        // Arrange
        SessionEntity cart1 = new SessionEntity("1");
        SessionEntity cart2 = new SessionEntity("2");
        SessionEntity savedCart1 = entityManager.persist(cart1);
        SessionEntity savedCart2 = entityManager.persist(cart2);

        OrderEntity order1 = new OrderEntity(savedCart1);
        OrderEntity order2 = new OrderEntity(savedCart1); // Два заказа для cart1
        OrderEntity order3 = new OrderEntity(savedCart2); // Один заказ для cart2
        entityManager.persist(order1);
        entityManager.persist(order2);
        entityManager.persist(order3);
        entityManager.flush();

        // Act
        List<OrderEntity> orderList1 = orderRepository.findBySessionId(cart1.getId());
        List<OrderEntity> orderList2 = orderRepository.findBySessionId(cart2.getId());

        // Assert
        assertEquals(2, orderList1.size());
        assertEquals(1, orderList2.size());
        assertTrue(orderList1.stream().allMatch(o -> o.getSession().getId().equals(cart1.getId())));
        assertTrue(orderList2.stream().allMatch(o -> o.getSession().getId().equals(cart2.getId())));
    }

    @Test
    void findByCartId_shouldReturnEmptyListForNonExistingSession() {
        // Act
        List<OrderEntity> result = orderRepository.findBySessionId(0L);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @Transactional
    void shouldPersistOrderWithDetails() {
        // Arrange
        SessionEntity cart = new SessionEntity("1");
        ItemEntity item1 = new ItemEntity("title1", "desc1", "img1.jpg", BigDecimal.ONE);
        ItemEntity item2 = new ItemEntity("title2", "desc2", "img2.jpg", BigDecimal.TWO);

        // Создаем детали корзины
        entityManager.persist(item1);
        entityManager.persist(item2);

        CartItemEntity detail1 = new CartItemEntity(cart, item1, 1, item1.getPrice());
        CartItemEntity detail2 = new CartItemEntity(cart, item2, 2, item2.getPrice());
        cart.getItems().put(item1, detail1);
        cart.getItems().put(item2, detail2);
        entityManager.persist(cart);

        // Act
        OrderEntity order = new OrderEntity(cart);
        OrderEntity savedOrder = orderRepository.save(order);

        // Assert
        assertNotNull(savedOrder.getId());
        assertEquals(cart.getId(), savedOrder.getSession().getId());
        assertEquals(2, savedOrder.getItems().size());
    }

    @Test
    @Transactional
    void shouldCascadePersistOrderDetails() {
        // Arrange
        SessionEntity cart = new SessionEntity("1");
        ItemEntity item = new ItemEntity("title", "desc1", "img1.jpg", BigDecimal.ONE);
        entityManager.persist(cart);
        entityManager.persist(item);

        CartItemEntity cartDetail = new CartItemEntity(cart, item, 1, item.getPrice());
        cart.getItems().put(item, cartDetail);
        entityManager.persist(cart);

        // Act
        OrderEntity order = new OrderEntity(cart);
        OrderEntity savedOrder = orderRepository.save(order);
        entityManager.flush();
        entityManager.clear();

        // Assert
        OrderEntity foundOrder = entityManager.find(OrderEntity.class, savedOrder.getId());
        assertEquals(1, foundOrder.getItems().size());
    }

    @Test
    void shouldNotAllowNullCart() {
        // Arrange
        OrderEntity order = new OrderEntity();

        // Act & Assert
        assertThrows(Exception.class, () -> {
            orderRepository.saveAndFlush(order);
        });
    }
}
