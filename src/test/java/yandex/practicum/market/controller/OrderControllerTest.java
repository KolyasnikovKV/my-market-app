package yandex.practicum.market.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;
import yandex.practicum.market.dto.factory.ItemDtoFactory;
import yandex.practicum.market.entity.SessionEntity;
import yandex.practicum.market.entity.CartItemEntity;
import yandex.practicum.market.entity.ItemEntity;
import yandex.practicum.market.entity.OrderEntity;
import yandex.practicum.market.dto.OrderDto;
import yandex.practicum.market.dto.factory.OrderDtoFactory;
import yandex.practicum.market.service.SessionService;
import yandex.practicum.market.service.OrderService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import({OrderDtoFactory.class, ItemDtoFactory.class})
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private SessionService sessionService;

    @MockitoBean
    private HttpSession session;

    @MockitoBean
    private Model model;

    private SessionEntity testCart;
    private ItemEntity testItem;


    @Test
    void buyItems_ShouldRedirectToOrderPage() throws Exception {
        // Подготовка данных корзины
        String sessionId = "1";
        testItem = new ItemEntity(1L, "Item", "Desc", "img.jpg", BigDecimal.ONE);
        testCart = new SessionEntity(1L, sessionId);

        when(session.getId()).thenReturn(sessionId);
        when(sessionService.getOrCreateSessionById(sessionId)).thenReturn(testCart);

        CartItemEntity cartDetail = new CartItemEntity(testCart, testItem, 2, testItem.getPrice());
        testCart.getItems().put(testItem, cartDetail);

        OrderEntity order = new OrderEntity(testCart);
        order.setId(1L);

        when(session.getId()).thenReturn(sessionId);
        when(sessionService.getOrCreateSessionById(sessionId)).thenReturn(testCart);
        when(orderService.buy(testCart)).thenReturn(order);

        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        // Act & Assert
        mockMvc.perform(post("/buy").session(mockSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/1?newOrder=true"));

        verify(sessionService).clear(testCart);
    }

    @Test
    void showOrders_ShouldReturnOrdersView() throws Exception {
        // Arrange
        String sessionId = "2";
        SessionEntity sessionEntity = new SessionEntity();
        List<OrderEntity> orders = new ArrayList<>();
        OrderEntity order = new OrderEntity();
        orders.add(order);
        OrderDto orderDto = new OrderDtoFactory(new ItemDtoFactory()).of(order, BigDecimal.ZERO);

        when(session.getId()).thenReturn(sessionId);
        when(sessionService.getOrCreateSessionById(sessionId)).thenReturn(sessionEntity);
        when(orderService.getAllOrdersBySessionId(sessionEntity.getId())).thenReturn(orders);
        when(orderService.getOrderTotalCost(order.getId())).thenReturn(BigDecimal.ZERO);

        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        // Act
        mockMvc.perform(get("/orders").session(mockSession))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attribute("orders", List.of(orderDto)));
    }

    @Test
    void showOrder_ShouldReturnOrderView() throws Exception {
        // Arrange
        String sessionId = "3";
        Long orderId = 1L;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);

        when(orderService.getOrder(orderId)).thenReturn(order);
        when(orderService.getOrderTotalCost(order.getId())).thenReturn(BigDecimal.TEN);

        // Act
        //String viewName = orderController.showOrder(orderId, false, model);
        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        mockMvc.perform(
                get("/orders/1").session(mockSession))
                .andExpect(status().isOk())
                .andExpect(view().name("order"))
                .andExpect(model().attributeExists("order"))
                .andExpect(model().attribute("newOrder", false)
                );
    }

    @Test
    void showOrder_ShouldThrowNoSuchElementException_WhenOrderNotFound() throws Exception {
        // Arrange
        String sessionId = "4";
        when(orderService.getOrder(0L)).thenThrow(NoSuchElementException.class);

        // Act & Assert
        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        mockMvc.perform(
                get("/orders/0").session(mockSession))
                .andExpect(status().isNotFound());
    }
}
