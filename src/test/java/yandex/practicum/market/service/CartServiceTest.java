package yandex.practicum.market.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import yandex.practicum.market.entity.CartEntity;
import yandex.practicum.market.entity.CartItemEntity;
import yandex.practicum.market.entity.ItemEntity;
import yandex.practicum.market.repository.CartItemRepository;
import yandex.practicum.market.repository.CartRepository;
import yandex.practicum.market.types.ActionType;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    void save_shouldCallRepositorySave() {
        // Arrange
        CartEntity cart = new CartEntity("1");
        when(cartRepository.save(cart)).thenReturn(cart);

        // Act
        CartEntity result = cartService.save(cart);

        // Assert
        assertNotNull(result);
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void clear_shouldDeleteAllCartDetails() {
        // Arrange
        CartEntity cart = new CartEntity(1L, "1");

        // Act
        cartService.clear(cart);

        // Assert
        verify(cartItemRepository, times(1)).deleteAllByCartId(1L);
    }

    @Test
    void updateCart_shouldIncreaseQuantityWhenActionIsPlusAndDetailExists() {
        // Arrange
        CartEntity cart = new CartEntity(1L, "1");
        ItemEntity item = new ItemEntity(1L, "title1", "desc1", "img1.jpg", BigDecimal.TEN);
        CartItemEntity cartDetail = new CartItemEntity(cart, item, 1, BigDecimal.TEN);
        cart.getItems().put(item, cartDetail);

        when(cartRepository.save(cart)).thenReturn(cart);

        // Act
        cartService.updateCart(cart, item, ActionType.PLUS);

        // Assert
        assertEquals(2, cartDetail.getQuantity());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void updateCart_shouldDecreaseQuantityWhenActionIsMinusAndQuantityRemainsPositive() {
        // Arrange
        CartEntity cart = new CartEntity(1L, "1");
        ItemEntity item = new ItemEntity(1L, "title1", "desc1", "img1.jpg", BigDecimal.TEN);
        CartItemEntity cartDetail = new CartItemEntity(cart, item, 2, BigDecimal.TEN);
        cart.getItems().put(item, cartDetail);

        when(cartRepository.save(cart)).thenReturn(cart);

        // Act
        cartService.updateCart(cart, item, ActionType.MINUS);

        // Assert
        assertEquals(1, cartDetail.getQuantity());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void updateCart_shouldRemoveDetailWhenActionIsMinusAndQuantityBecomesZero() {
        // Arrange
        CartEntity cart = new CartEntity(1L, "1");
        ItemEntity item = new ItemEntity(1L, "title1", "desc1", "img1.jpg", BigDecimal.TEN);
        CartItemEntity cartDetail = new CartItemEntity(cart, item, 1, BigDecimal.TEN);
        cart.getItems().put(item, cartDetail);

        when(cartRepository.save(cart)).thenReturn(cart);

        // Act
        cartService.updateCart(cart, item, ActionType.MINUS);

        // Assert
        assertFalse(cart.getItems().containsKey(item));
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void updateCart_shouldRemoveDetailWhenActionIsDelete() {
        // Arrange
        CartEntity cart = new CartEntity(1L, "1");
        ItemEntity item = new ItemEntity(1L, "title1", "desc1", "img1.jpg", BigDecimal.TEN);
        CartItemEntity cartDetail = new CartItemEntity(cart, item, 2, BigDecimal.TEN);
        cart.getItems().put(item, cartDetail);

        when(cartRepository.save(cart)).thenReturn(cart);

        // Act
        cartService.updateCart(cart, item, ActionType.DELETE);

        // Assert
        assertFalse(cart.getItems().containsKey(item));
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void updateCart_shouldAddNewDetailWhenActionIsPlusAndDetailNotExists() {
        // Arrange
        CartEntity cart = new CartEntity(1L, "1");
        ItemEntity item = new ItemEntity(1L, "title1", "desc1", "img1.jpg", BigDecimal.TEN);

        when(cartRepository.save(cart)).thenReturn(cart);

        // Act
        cartService.updateCart(cart, item, ActionType.PLUS);

        // Assert
        assertTrue(cart.getItems().containsKey(item));
        assertEquals(1, cart.getItems().get(item).getQuantity());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void getOrCreateCartBySessionId_shouldReturnExistingCartBy() {
        // Arrange
        String sessionId = "1";
        CartEntity existingCart = new CartEntity(1L, sessionId);
        when(cartRepository.findBySessionId(sessionId)).thenReturn(Optional.of(existingCart));

        // Act
        CartEntity result = cartService.getOrCreateSessionById(sessionId);

        // Assert
        assertEquals(existingCart, result);
        verify(cartRepository, times(1)).findBySessionId(sessionId);
        verify(cartRepository, never()).save(any());
    }

    @Test
    void getOrCreateCartBySessionId_shouldCreateNewCartWhenNotExistsBy() {
        // Arrange
        String sessionId = "1";
        CartEntity newCart = new CartEntity(sessionId);
        when(cartRepository.findBySessionId(sessionId)).thenReturn(Optional.empty());
        when(cartRepository.save(any(CartEntity.class))).thenReturn(newCart);

        // Act
        CartEntity result = cartService.getOrCreateSessionById(sessionId);

        // Assert
        assertNotNull(result);
        assertEquals(sessionId, result.getSessionId());
        verify(cartRepository, times(1)).findBySessionId(sessionId);
        verify(cartRepository, times(1)).save(any(CartEntity.class));
    }

    @Test
    void saveCart_shouldCallRepositorySave() {
        // Arrange
        CartEntity cart = new CartEntity("1");
        when(cartRepository.save(cart)).thenReturn(cart);

        // Act
        CartEntity result = cartService.saveSession(cart);

        // Assert
        assertNotNull(result);
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void getCartTotalCost_BySessionId_shouldReturnSumFromRepository() {
        // Arrange
        Long cartId = 1L;
        CartEntity cartEntity = new CartEntity(cartId, "1");
        BigDecimal expectedSum = new BigDecimal("100.00");
        when(cartItemRepository.sumTotalCostInCartBySessionId(cartId)).thenReturn(Optional.of(expectedSum));

        // Act
        BigDecimal result = cartService.getCartTotalCostBySessionId(cartEntity.getId());

        // Assert
        assertEquals(expectedSum, result);
        verify(cartItemRepository, times(1)).sumTotalCostInCartBySessionId(cartId);
    }

    @Test
    void getCartTotalCost_BySessionId_shouldReturnZeroWhenNoDetails() {
        // Arrange
        Long cartId = 1L;
        CartEntity cartEntity = new CartEntity(cartId, "1");
        when(cartItemRepository.sumTotalCostInCartBySessionId(cartId)).thenReturn(Optional.empty());

        // Act
        BigDecimal result = cartService.getCartTotalCostBySessionId(cartEntity.getId());

        // Assert
        assertEquals(BigDecimal.ZERO, result);
        verify(cartItemRepository, times(1)).sumTotalCostInCartBySessionId(cartId);
    }
}
