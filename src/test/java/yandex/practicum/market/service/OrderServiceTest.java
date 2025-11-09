package yandex.practicum.market.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import yandex.practicum.market.entity.SessionEntity;
import yandex.practicum.market.entity.CartItemEntity;
import yandex.practicum.market.entity.ItemEntity;
import yandex.practicum.market.entity.OrderEntity;
import yandex.practicum.market.repository.OrderItemRepository;
import yandex.practicum.market.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderService orderService;


    @Test
    void buy_shouldCreateOrderFromCart() {
        SessionEntity session = Mockito.mock(SessionEntity.class);
        Long sessionId = 31L;
        when(session.getId()).thenReturn(sessionId);

        ItemEntity item1 = new ItemEntity(1L, "title1", "desc1", "img1.jpg", new BigDecimal("10.00"));
        ItemEntity item2 = new ItemEntity(2L, "title2", "desc2", "img2.jpg", new BigDecimal("20.00"));

        Map<ItemEntity, CartItemEntity> cartDetails = new HashMap<>();
        cartDetails.put(item1, new CartItemEntity(session, item1, 2, new BigDecimal("10.00")));
        cartDetails.put(item2, new CartItemEntity(session, item2, 1, new BigDecimal("20.00")));

        when(session.getItems()).thenReturn(cartDetails);

        OrderEntity expectedOrder = new OrderEntity(session);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(expectedOrder);

        OrderEntity result = orderService.buy(session);

        assertNotNull(result);
        assertEquals(session, result.getSession());
        assertEquals(2, result.getItems().size());
        verify(orderRepository, times(1)).save(any(OrderEntity.class));
    }

    @Test
    void getAllOrders_shouldReturnOrdersBySessionIdForCart() {
        SessionEntity session = Mockito.mock(SessionEntity.class);
        Long sessionId = 32L;
        when(session.getId()).thenReturn(sessionId);

        List<OrderEntity> expectedOrders = Arrays.asList(
                new OrderEntity(session),
                new OrderEntity(session)
        );

        when(orderRepository.findBySessionId(sessionId)).thenReturn(expectedOrders);

        List<OrderEntity> result = orderService.getAllOrdersBySessionId(session.getId());

        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findBySessionId(sessionId);
    }

    @Test
    void getOrder_shouldReturnOrderWhenExists() {
        SessionEntity session = Mockito.mock(SessionEntity.class);
        Long orderId = 1L;
        OrderEntity expectedOrder = new OrderEntity(session);
        expectedOrder.setId(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(expectedOrder));

        OrderEntity result = orderService.getOrder(orderId);

        assertEquals(expectedOrder, result);
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void getOrder_shouldReturnEmptyWhenNotExists() {
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenThrow(NoSuchElementException.class);

        assertThrows(NoSuchElementException.class, () -> orderService.getOrder(orderId));
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void getOrderTotalCost_shouldReturnSumForOrder() {

        Long orderId = 1L;
        SessionEntity session = Mockito.mock(SessionEntity.class);
        OrderEntity order = new OrderEntity(session);
        order.setId(orderId);

        BigDecimal expectedSum = new BigDecimal("50.00");
        when(orderItemRepository.sumTotalCostInOrder(orderId)).thenReturn(Optional.of(expectedSum));


        BigDecimal result = orderService.getOrderTotalCost(order.getId());


        assertEquals(expectedSum, result);
        verify(orderItemRepository, times(1)).sumTotalCostInOrder(orderId);
    }

    @Test
    void getOrderTotalCost_shouldReturnZeroWhenNoDetails() {

        Long orderId = 1L;
        SessionEntity session = Mockito.mock(SessionEntity.class);
        OrderEntity order = new OrderEntity(session);
        order.setId(orderId);

        when(orderItemRepository.sumTotalCostInOrder(orderId)).thenReturn(Optional.empty());


        BigDecimal result = orderService.getOrderTotalCost(order.getId());


        assertEquals(BigDecimal.ZERO, result);
        verify(orderItemRepository, times(1)).sumTotalCostInOrder(orderId);
    }
}