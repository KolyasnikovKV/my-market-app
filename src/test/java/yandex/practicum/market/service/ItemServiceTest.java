package yandex.practicum.market.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import yandex.practicum.market.entity.ItemEntity;
import yandex.practicum.market.repository.ItemRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    @Test
    void getItems_shouldReturnPageOfItems() {
        // Подготовка тестовых данных
        ItemEntity testItem = new ItemEntity(1L, "title1", "desc1", "image1.jpg", BigDecimal.valueOf(1));
        Page<ItemEntity> expectedPage = new PageImpl<>(Collections.singletonList(testItem));

        when(itemRepository.findAllBySearchTerm(anyString(), any(Pageable.class))).thenReturn(expectedPage);

        // Тестируемое действие
        Page<ItemEntity> result = itemService.getItems("test", Pageable.unpaged());

        // Проверка результатов
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testItem, result.getContent().get(0));
    }

    @Test
    void getItems_withNullSearchTerm_shouldReturnPageOfItems() {
        // Подготовка тестовых данных
        ItemEntity testItem = new ItemEntity(1L, "title1", "desc1", "image1.jpg", BigDecimal.valueOf(1));
        Page<ItemEntity> expectedPage = new PageImpl<>(Collections.singletonList(testItem));
        //
        when(itemRepository.findAllBySearchTerm(nullable(String.class), any(Pageable.class))).thenReturn(expectedPage);

        // Тестируемое действие
        Page<ItemEntity> result = itemService.getItems(null, Pageable.unpaged());

        // Проверка результатов
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getItem_shouldReturnItemWhenExists() {
        // Подготовка тестовых данных
        Long itemId = 1L;
        ItemEntity expectedItem = new ItemEntity(1L, "title1", "desc1", "image1.jpg", BigDecimal.valueOf(1));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));

        // Тестируемое действие
        ItemEntity result = itemService.getItem(itemId);

        // Проверка результатов
        assertEquals(expectedItem, result);
    }

    @Test
    void getItem_shouldReturnEmptyWhenNotExists() {
        // Подготовка тестовых данных
        when(itemRepository.findById(anyLong())).thenThrow(NoSuchElementException.class);

        assertThrows(NoSuchElementException.class, () -> itemService.getItem(anyLong()));
    }
}
