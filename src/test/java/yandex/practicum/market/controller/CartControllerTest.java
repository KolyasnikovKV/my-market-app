package yandex.practicum.market.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import yandex.practicum.market.dto.factory.ItemDtoFactory;
import yandex.practicum.market.entity.SessionEntity;
import yandex.practicum.market.entity.ItemEntity;
import yandex.practicum.market.service.CartOperationService;
import yandex.practicum.market.service.SessionService;
import yandex.practicum.market.service.ItemService;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import yandex.practicum.market.entity.CartItemEntity;
import yandex.practicum.market.types.ActionType;

@WebMvcTest(CartController.class)
@Import({ItemDtoFactory.class, CartOperationService.class})
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SessionService sessionService;

    @MockitoBean
    private ItemService itemService;

    @MockitoBean
    private HttpSession session;

    private final String sessionId = "1";
    private SessionEntity testSession;
    private ItemEntity testItem;


    @Test
    void showCart_shouldReturnCartViewWithItems() throws Exception {
        // Подготовка данных корзины
        testItem = new ItemEntity(1L, "Item", "Desc", "img.jpg", BigDecimal.ONE);
        testSession = new SessionEntity(1L, sessionId);

        when(session.getId()).thenReturn(sessionId);
        when(sessionService.getOrCreateSessionById(sessionId)).thenReturn(testSession);

        CartItemEntity cartDetail = new CartItemEntity(testSession, testItem, 2, testItem.getPrice());
        testSession.getItems().put(testItem, cartDetail);

        when(sessionService.getCartTotalCostBySessionId(testSession.getId())).thenReturn(BigDecimal.valueOf(2));

        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        // Выполнение и проверка
        mockMvc.perform(get("/cart/items").session(mockSession))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attribute("total", BigDecimal.valueOf(2)))
                .andExpect(model().attribute("empty", false))
                .andExpect(model().attribute("items", hasSize(1)));
    }

    @Test
    void showCart_shouldReturnEmptyCart() throws Exception {
        testSession = new SessionEntity(1L, sessionId);

        when(session.getId()).thenReturn(sessionId);
        when(sessionService.getOrCreateSessionById(sessionId)).thenReturn(testSession);

        when(sessionService.getCartTotalCostBySessionId(testSession.getId()))
                .thenReturn(BigDecimal.ZERO);

        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        mockMvc.perform(get("/cart/items").session(mockSession))
                .andExpect(status().isOk())
                .andExpect(model().attribute("empty", true))
                .andExpect(model().attribute("items", empty()));
    }

    @Test
    void updateCartByMainPage_shouldRedirectToMain() throws Exception {
        testItem = new ItemEntity(1L, "Item", "Desc", "img.jpg", BigDecimal.ONE);
        testSession = new SessionEntity(1L, sessionId);

        when(session.getId()).thenReturn(sessionId);
        when(sessionService.getOrCreateSessionById(sessionId)).thenReturn(testSession);

        when(itemService.getItem(1L)).thenReturn(testItem);

        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        mockMvc.perform(post("/items/1")
                        .session(mockSession)
                        .param("action", "PLUS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items/1"));

        verify(sessionService).updateCart(eq(testSession), eq(testItem), eq(ActionType.PLUS));
    }

    @Test
    void updateCartByCartPage_shouldRedirectToCart() throws Exception {
        testItem = new ItemEntity(1L, "Item", "Desc", "img.jpg", BigDecimal.ONE);
        testSession = new SessionEntity(1L, sessionId);

        when(session.getId()).thenReturn(sessionId);
        when(sessionService.getOrCreateSessionById(sessionId)).thenReturn(testSession);

        when(itemService.getItem(1L)).thenReturn(testItem);

        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        mockMvc.perform(post("/cart/items")
                        .session(mockSession)
                        .param("id", "1")
                        .param("action", "DELETE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart/items"));

        verify(sessionService).updateCart(eq(testSession), eq(testItem), eq(ActionType.DELETE));
    }

    @Test
    void updateCartByItemPage_shouldRedirectToItemPage() throws Exception {
        testItem = new ItemEntity(1L, "Item", "Desc", "img.jpg", BigDecimal.ONE);
        testSession = new SessionEntity(1L, sessionId);

        when(session.getId()).thenReturn(sessionId);
        when(sessionService.getOrCreateSessionById(sessionId)).thenReturn(testSession);

        when(itemService.getItem(1L)).thenReturn(testItem);

        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        mockMvc.perform(post("/items/1")
                        .session(mockSession)
                        .param("action", "MINUS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items/1"));

        verify(sessionService).updateCart(eq(testSession), eq(testItem), eq(ActionType.MINUS));
    }

    @Test
    void updateCart_shouldThrowExceptionWhenItemNotFound() throws Exception {
        testSession = new SessionEntity(1L, sessionId);

        when(session.getId()).thenReturn(sessionId);
        when(sessionService.getOrCreateSessionById(sessionId)).thenReturn(testSession);

        when(itemService.getItem(0L)).thenReturn(null);

        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        mockMvc.perform(post("/cart/items/0")
                        .session(mockSession)
                        .param("action", "PLUS"))
                .andExpect(status().isNotFound());
    }
}
