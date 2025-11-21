package yandex.practicum.market.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
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
class CartItemRepositoryTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @Transactional
    void deleteAllBySessionId_shouldRemoveAllDetailsForCart() {
        // Arrange
        CartEntity cart = new CartEntity("1");
        entityManager.persist(cart);

        ItemEntity item1 = new ItemEntity("title1", "desc1", "img1.jpg", BigDecimal.ONE);
        ItemEntity item2 = new ItemEntity("title2", "desc2", "img2.jpg", BigDecimal.TWO);
        entityManager.persist(item1);
        entityManager.persist(item2);

        CartItemEntity detail1 = new CartItemEntity(cart, item1, 1, item1.getPrice());
        CartItemEntity detail2 = new CartItemEntity(cart, item2, 2, item2.getPrice());
        entityManager.persist(detail1);
        entityManager.persist(detail2);
        entityManager.flush();
        entityManager.refresh(cart);

        // Act
        cartItemRepository.deleteAllByCartId(cart.getId());
        entityManager.flush();

        // Assert
        assertEquals(0, cartItemRepository.count());
    }

    @Test
    @Transactional
    void sumTotalCostInCart_shouldReturnCorrectSumBySessionId() {
        // Arrange
        CartEntity cart = new CartEntity("session1");
        entityManager.persist(cart);

        ItemEntity item1 = new ItemEntity("title1", "desc1", "img1.jpg", BigDecimal.ONE);
        ItemEntity item2 = new ItemEntity("title2", "desc2", "img2.jpg", BigDecimal.TWO);
        entityManager.persist(item1);
        entityManager.persist(item2);

        CartItemEntity detail1 = new CartItemEntity(cart, item1, 2, item1.getPrice()); // 1 * 2 = 2
        CartItemEntity detail2 = new CartItemEntity(cart, item2, 1, item2.getPrice()); // 2 * 1 = 2
        entityManager.persist(detail1);
        entityManager.persist(detail2);
        entityManager.flush();

        // Act
        Optional<BigDecimal> result = cartItemRepository.sumTotalCostInCartBySessionId(cart.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("4.00"), result.get());
    }

    @Test
    @Transactional
    void sumTotalCostInCart_shouldReturnEmptyForEmptyCartBySessionId() {
        // Arrange
        CartEntity cart = new CartEntity("1");
        entityManager.persist(cart);
        entityManager.flush();

        // Act
        Optional<BigDecimal> result = cartItemRepository.sumTotalCostInCartBySessionId(cart.getId());

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @Transactional
    void shouldPersistCartDetailWithCompositeKey() {
        // Arrange
        CartEntity cart = new CartEntity("1");
        entityManager.persist(cart);

        ItemEntity item = new ItemEntity("title1", "desc1", "img1.jpg", BigDecimal.ONE);
        entityManager.persist(item);

        CartItemEntity detail = new CartItemEntity(cart, item, 1, item.getPrice());

        // Act
        CartItemEntity savedDetail = cartItemRepository.save(detail);
        entityManager.flush();

        // Assert
        assertNotNull(savedDetail);
        assertEquals(cart.getId(), savedDetail.getId().getCartId());
        assertEquals(item.getId(), savedDetail.getId().getItemId());
    }
}