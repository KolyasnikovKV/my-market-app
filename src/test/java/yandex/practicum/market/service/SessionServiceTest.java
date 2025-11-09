package yandex.practicum.market.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import yandex.practicum.market.entity.SessionEntity;
import yandex.practicum.market.entity.CartItemEntity;
import yandex.practicum.market.entity.ItemEntity;
import yandex.practicum.market.repository.CartItemRepository;
import yandex.practicum.market.repository.SessionRepository;
import yandex.practicum.market.types.ActionType;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private SessionService sessionService;

    @Test
    void save_shouldCallRepositorySave() {
        // Arrange
        SessionEntity cart = new SessionEntity("1");
        when(sessionRepository.save(cart)).thenReturn(cart);

        // Act
        SessionEntity result = sessionService.save(cart);

        // Assert
        assertNotNull(result);
        verify(sessionRepository, times(1)).save(cart);
    }

    @Test
    void clear_shouldDeleteAllCartDetails() {
        // Arrange
        SessionEntity cart = new SessionEntity(1L, "1");

        // Act
        sessionService.clear(cart);

        // Assert
        verify(cartItemRepository, times(1)).deleteAllByCartId(1L);
    }

    @Test
    void updateCart_shouldIncreaseQuantityWhenActionIsPlusAndDetailExists() {
        // Arrange
        SessionEntity cart = new SessionEntity(1L, "1");
        ItemEntity item = new ItemEntity(1L, "title1", "desc1", "img1.jpg", BigDecimal.TEN);
        CartItemEntity cartDetail = new CartItemEntity(cart, item, 1, BigDecimal.TEN);
        cart.getItems().put(item, cartDetail);

        when(sessionRepository.save(cart)).thenReturn(cart);

        // Act
        sessionService.updateCart(cart, item, ActionType.PLUS);

        // Assert
        assertEquals(2, cartDetail.getQuantity());
        verify(sessionRepository, times(1)).save(cart);
    }

    @Test
    void updateCart_shouldDecreaseQuantityWhenActionIsMinusAndQuantityRemainsPositive() {
        // Arrange
        SessionEntity cart = new SessionEntity(1L, "1");
        ItemEntity item = new ItemEntity(1L, "title1", "desc1", "img1.jpg", BigDecimal.TEN);
        CartItemEntity cartDetail = new CartItemEntity(cart, item, 2, BigDecimal.TEN);
        cart.getItems().put(item, cartDetail);

        when(sessionRepository.save(cart)).thenReturn(cart);

        // Act
        sessionService.updateCart(cart, item, ActionType.MINUS);

        // Assert
        assertEquals(1, cartDetail.getQuantity());
        verify(sessionRepository, times(1)).save(cart);
    }

    @Test
    void updateCart_shouldRemoveDetailWhenActionIsMinusAndQuantityBecomesZero() {
        // Arrange
        SessionEntity cart = new SessionEntity(1L, "1");
        ItemEntity item = new ItemEntity(1L, "title1", "desc1", "img1.jpg", BigDecimal.TEN);
        CartItemEntity cartDetail = new CartItemEntity(cart, item, 1, BigDecimal.TEN);
        cart.getItems().put(item, cartDetail);

        when(sessionRepository.save(cart)).thenReturn(cart);

        // Act
        sessionService.updateCart(cart, item, ActionType.MINUS);

        // Assert
        assertFalse(cart.getItems().containsKey(item));
        verify(sessionRepository, times(1)).save(cart);
    }

    @Test
    void updateCart_shouldRemoveDetailWhenActionIsDelete() {
        // Arrange
        SessionEntity cart = new SessionEntity(1L, "1");
        ItemEntity item = new ItemEntity(1L, "title1", "desc1", "img1.jpg", BigDecimal.TEN);
        CartItemEntity cartDetail = new CartItemEntity(cart, item, 2, BigDecimal.TEN);
        cart.getItems().put(item, cartDetail);

        when(sessionRepository.save(cart)).thenReturn(cart);

        // Act
        sessionService.updateCart(cart, item, ActionType.DELETE);

        // Assert
        assertFalse(cart.getItems().containsKey(item));
        verify(sessionRepository, times(1)).save(cart);
    }

    @Test
    void updateCart_shouldAddNewDetailWhenActionIsPlusAndDetailNotExists() {
        // Arrange
        SessionEntity cart = new SessionEntity(1L, "1");
        ItemEntity item = new ItemEntity(1L, "title1", "desc1", "img1.jpg", BigDecimal.TEN);

        when(sessionRepository.save(cart)).thenReturn(cart);

        // Act
        sessionService.updateCart(cart, item, ActionType.PLUS);

        // Assert
        assertTrue(cart.getItems().containsKey(item));
        assertEquals(1, cart.getItems().get(item).getQuantity());
        verify(sessionRepository, times(1)).save(cart);
    }

    @Test
    void getOrCreateCartBySessionId_shouldReturnExistingCartBy() {
        // Arrange
        String sessionId = "1";
        SessionEntity existingCart = new SessionEntity(1L, sessionId);
        when(sessionRepository.findBySessionId(sessionId)).thenReturn(Optional.of(existingCart));

        // Act
        SessionEntity result = sessionService.getOrCreateSessionById(sessionId);

        // Assert
        assertEquals(existingCart, result);
        verify(sessionRepository, times(1)).findBySessionId(sessionId);
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void getOrCreateCartBySessionId_shouldCreateNewCartWhenNotExistsBy() {
        // Arrange
        String sessionId = "1";
        SessionEntity newCart = new SessionEntity(sessionId);
        when(sessionRepository.findBySessionId(sessionId)).thenReturn(Optional.empty());
        when(sessionRepository.save(any(SessionEntity.class))).thenReturn(newCart);

        // Act
        SessionEntity result = sessionService.getOrCreateSessionById(sessionId);

        // Assert
        assertNotNull(result);
        assertEquals(sessionId, result.getSessionId());
        verify(sessionRepository, times(1)).findBySessionId(sessionId);
        verify(sessionRepository, times(1)).save(any(SessionEntity.class));
    }

    @Test
    void saveCart_shouldCallRepositorySave() {
        // Arrange
        SessionEntity cart = new SessionEntity("1");
        when(sessionRepository.save(cart)).thenReturn(cart);

        // Act
        SessionEntity result = sessionService.saveSession(cart);

        // Assert
        assertNotNull(result);
        verify(sessionRepository, times(1)).save(cart);
    }

    @Test
    void getCartTotalCost_BySessionId_shouldReturnSumFromRepository() {
        // Arrange
        Long cartId = 1L;
        SessionEntity sessionEntity = new SessionEntity(cartId, "1");
        BigDecimal expectedSum = new BigDecimal("100.00");
        when(cartItemRepository.sumTotalCostInCartBySessionId(cartId)).thenReturn(Optional.of(expectedSum));

        // Act
        BigDecimal result = sessionService.getCartTotalCostBySessionId(sessionEntity.getId());

        // Assert
        assertEquals(expectedSum, result);
        verify(cartItemRepository, times(1)).sumTotalCostInCartBySessionId(cartId);
    }

    @Test
    void getCartTotalCost_BySessionId_shouldReturnZeroWhenNoDetails() {
        // Arrange
        Long cartId = 1L;
        SessionEntity sessionEntity = new SessionEntity(cartId, "1");
        when(cartItemRepository.sumTotalCostInCartBySessionId(cartId)).thenReturn(Optional.empty());

        // Act
        BigDecimal result = sessionService.getCartTotalCostBySessionId(sessionEntity.getId());

        // Assert
        assertEquals(BigDecimal.ZERO, result);
        verify(cartItemRepository, times(1)).sumTotalCostInCartBySessionId(cartId);
    }
}
