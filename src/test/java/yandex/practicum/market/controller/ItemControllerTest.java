package yandex.practicum.market.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import jakarta.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.hasSize;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.springframework.ui.Model;
import yandex.practicum.market.dto.factory.ItemDtoFactory;
import yandex.practicum.market.entity.CartEntity;
import yandex.practicum.market.entity.CartItemEntity;
import yandex.practicum.market.entity.ItemEntity;
import yandex.practicum.market.dto.ItemDto;
import yandex.practicum.market.service.ItemOperationService;
import yandex.practicum.market.service.CartService;
import yandex.practicum.market.service.ItemService;

import java.math.BigDecimal;
import java.util.*;

@WebMvcTest(ItemController.class)
@Import({ItemDtoFactory.class, ItemOperationService.class})
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemService itemService;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private HttpSession session;

    @MockitoBean
    private Model model;


    @Test
    void showItems_shouldReturnMainViewWithItems() throws Exception {
        // Подготовка тестовых данных
        String sessionId = "1";

        ItemEntity item1 = new ItemEntity("Item 1", "Desc 1", null, BigDecimal.ONE);
        ItemEntity item2 = new ItemEntity("Item 2", "Desc 2", null, BigDecimal.TWO);
        List<ItemEntity> testItems = List.of(item1, item2);
        Page<ItemEntity> page = new PageImpl<>(testItems);

        CartEntity cart = new CartEntity(1L, sessionId);
        CartItemEntity cartDetail1 = new CartItemEntity(cart, item1, 1, item1.getPrice());
        CartItemEntity cartDetail2 = new CartItemEntity(cart, item2, 2, item2.getPrice());

        cart.getItems().put(item1, cartDetail1);
        cart.getItems().put(item2, cartDetail2);

        when(itemService.getItems(anyString(), any(Pageable.class))).thenReturn(page);
        when(session.getId()).thenReturn(sessionId);
        when(cartService.getOrCreateSessionById(sessionId)).thenReturn(cart);

        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        // Выполнение и проверка
        mockMvc.perform(get("/items")
                        .session(mockSession)
                        .param("pageSize", "10")
                        .param("pageNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("search"))
                .andExpect(model().attributeExists("sort"))
                .andExpect(model().attributeExists("paging"));
    }

    @Test
    void showItem_shouldReturnItemViewWhenItemExists() throws Exception {
        // Подготовка тестовых данных
        String sessionId = "1";
        ItemEntity testItem = new ItemEntity(1L, "title1", "desc1", "image1.jpg", BigDecimal.valueOf(1.0));

        when(itemService.getItem(1L)).thenReturn(testItem);
        when(session.getId()).thenReturn(sessionId);
        when(cartService.getOrCreateSessionById(anyString())).thenReturn(new CartEntity(1L, sessionId));

        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        // Выполнение и проверка
        mockMvc.perform(get("/items/1").session(mockSession))
                .andExpect(status().isOk())
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("item"));
    }

    @Test
    public void showItem_shouldThrowExceptionWhenItemNotExists() throws Exception {
        Long nonExistentItemId = 0L;
        when(itemService.getItem(nonExistentItemId)).thenThrow(NoSuchElementException.class);

        MockHttpSession mockSession = new MockHttpSession(null, "1");

        mockMvc.perform(get("/items/0").session(mockSession))
                .andExpect(status().isNotFound());

        verify(itemService).getItem(nonExistentItemId);
        verifyNoInteractions(cartService);
    }

    @Test
    public void showItems_shouldSplitItemsIntoRowsCorrectly() throws Exception {
        // Arrange
        String sessionId = "1";

        List<ItemEntity> testItems = List.of(
                new ItemEntity("Item 1", "Desc 1", null, BigDecimal.valueOf(1)),
                new ItemEntity("Item 2", "Desc 2", null, BigDecimal.valueOf(2)),
                new ItemEntity("Item 3", "Desc 3", null, BigDecimal.valueOf(3)),
                new ItemEntity("Item 4", "Desc 4", null, BigDecimal.valueOf(4)),
                new ItemEntity("Item 5", "Desc 5", null, BigDecimal.valueOf(5))
        );

        Page<ItemEntity> page = new PageImpl<>(testItems);
        when(itemService.getItems(anyString(), any(Pageable.class))).thenReturn(page);
        when(session.getId()).thenReturn(sessionId);
        when(cartService.getOrCreateSessionById(sessionId)).thenReturn(new CartEntity(1L, sessionId));

        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        // Act
        mockMvc.perform(get("/items")
                        .session(mockSession)
                        .param("pageSize", "10")
                        .param("pageNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attribute("items", instanceOf(List.class)))
                .andExpect(model().attribute("items", hasSize(2))) // Ожидаем 2 строки (по 2 товара в каждой)
                .andDo(result -> {
                    // Детальная проверка структуры данных
                    List<List<ItemDto>> itemsInRows = (List<List<ItemDto>>) result.getModelAndView()
                            .getModel().get("items");

                    assertEquals(2, itemsInRows.size()); // Проверяем количество строк

                    // Проверяем первую строку (2 товара)
                    assertEquals(3, itemsInRows.get(0).size());
                    assertEquals("Item 1", itemsInRows.get(0).get(0).getTitle());
                    assertEquals("Item 2", itemsInRows.get(0).get(1).getTitle());
                    assertEquals("Item 3", itemsInRows.get(0).get(2).getTitle());

                    // Проверяем вторую строку (2 товара)
                    assertEquals(2, itemsInRows.get(1).size());
                    assertEquals("Item 4", itemsInRows.get(1).get(0).getTitle());
                    assertEquals("Item 5", itemsInRows.get(1).get(1).getTitle());
                }
                );

        // Проверка вызовов сервисов
        verify(itemService).getItems(eq(""), any(Pageable.class));
        verify(cartService).getOrCreateSessionById(sessionId);
    }

}
