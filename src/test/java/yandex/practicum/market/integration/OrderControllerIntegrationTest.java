package yandex.practicum.market.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import yandex.practicum.market.entity.*;
import yandex.practicum.market.repository.CartRepository;
import yandex.practicum.market.repository.ItemRepository;
import yandex.practicum.market.repository.OrderRepository;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class OrderControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;


    @Test
    @Transactional
    void buyItems_ShouldRedirectToOrderPage() throws Exception {
        // Подготовка данных корзины
        String sessionId = "11";
        ItemEntity item1 = new ItemEntity("Item 1", "Desc 1", "img1.jpg", BigDecimal.ONE);
        itemRepository.save(item1);
        CartEntity cart = new CartEntity(sessionId);
        cartRepository.save(cart);

        CartItemEntity cartDetail = new CartItemEntity(cart, item1, 2, item1.getPrice());
        cart.getItems().put(item1, cartDetail);

        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        // Act & Assert
        mockMvc.perform(post("/buy").session(mockSession))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @Transactional
    void showOrders_ShouldReturnOrdersView() throws Exception {
        String sessionId = "12";
        ItemEntity item1 = new ItemEntity("Item 1", "Desc 1", "img1.jpg", BigDecimal.ONE);
        itemRepository.save(item1);
        ItemEntity item2 = new ItemEntity("Item 2", "Desc 2", "img2.jpg", BigDecimal.TWO);
        itemRepository.save(item2);

        // Arrange
        CartEntity cart = new CartEntity(sessionId);
        cartRepository.save(cart);

        OrderEntity order1 = new OrderEntity(cart);
        OrderItemEntity orderDetail1 = new OrderItemEntity(order1, item1, 1, item1.getPrice());
        order1.getItems().put(item1, orderDetail1);
        OrderItemEntity orderDetail2 = new OrderItemEntity(order1, item2, 2, item1.getPrice());
        order1.getItems().put(item1, orderDetail2);
        orderRepository.save(order1);

        OrderEntity order2 = new OrderEntity(cart);
        OrderItemEntity orderDetail3 = new OrderItemEntity(order2, item1, 2, item1.getPrice());
        order2.getItems().put(item1, orderDetail3);
        orderRepository.save(order2);

        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        // Act
        mockMvc.perform(get("/orders").session(mockSession))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"));
    }

    @Test
    @Transactional
    void showOrder_ShouldReturnOrderView() throws Exception {
        String sessionId = "13";
        // Arrange
        ItemEntity item1 = new ItemEntity("Item 1", "Desc 1", "img1.jpg", BigDecimal.ONE);
        itemRepository.save(item1);
        ItemEntity item2 = new ItemEntity("Item 2", "Desc 2", "img2.jpg", BigDecimal.TWO);
        itemRepository.save(item2);

        // Arrange
        CartEntity cart = new CartEntity(sessionId);
        cartRepository.save(cart);

        OrderEntity order1 = new OrderEntity(cart);
        OrderItemEntity orderDetail1 = new OrderItemEntity(order1, item1, 1, item1.getPrice());
        order1.getItems().put(item1, orderDetail1);
        OrderItemEntity orderDetail2 = new OrderItemEntity(order1, item2, 2, item1.getPrice());
        order1.getItems().put(item1, orderDetail2);
        order1 = orderRepository.save(order1);
        Long orderId = order1.getId();

        // Act
        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        mockMvc.perform(
                        get("/orders/" + orderId).session(mockSession))
                .andExpect(status().isOk())
                .andExpect(view().name("order"))
                .andExpect(model().attributeExists("order"))
                .andExpect(model().attribute("newOrder", false)
                );
    }

    @Test
    @Transactional
    void showOrder_ShouldThrowNoSuchElementException_WhenOrderNotFound() throws Exception {
        // Arrange
        String sessionId = "14";
        CartEntity cart = new CartEntity(sessionId);
        cartRepository.save(cart);

        // Act & Assert
        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        mockMvc.perform(get("/orders/0").session(mockSession))
                .andExpect(status().isNotFound());
    }
}
