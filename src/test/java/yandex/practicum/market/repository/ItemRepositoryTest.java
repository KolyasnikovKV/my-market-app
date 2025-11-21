package yandex.practicum.market.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import yandex.practicum.market.entity.ItemEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    private Pageable pageable = PageRequest.of(0, 10);
    private String title1 = "title1";
    private String title2 = "title2";
    @BeforeEach
    void setUp() {
        ItemEntity item1 = new ItemEntity(null, title1, "desc1", "img1.jpg", BigDecimal.ONE);
        ItemEntity item2 = new ItemEntity(null, title2, "desc2", "img2.jpg", BigDecimal.TWO);
        itemRepository.saveAll(List.of(item1, item2));
    }

    @AfterEach
    void cleanUp() {
        itemRepository.deleteAll();
    }

    @Test
    void findAllBySearchTerm_shouldReturnAllItemsWhenSearchTermIsNull() {
        // Act
        Page<ItemEntity> result = itemRepository.findAllBySearchTerm(null, pageable);

        // Assert
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream()
                .map(ItemEntity::getTitle)
                .allMatch(title -> List.of(title1, title2).contains(title)));
    }

    @Test
    void findAllBySearchTerm_shouldFilterByTitle() {
        // Act
        Page<ItemEntity> result = itemRepository.findAllBySearchTerm("title1", pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("title1", result.getContent().get(0).getTitle());
    }

    @Test
    void findAllBySearchTerm_shouldFilterByDescription() {
        // Act
        Page<ItemEntity> result = itemRepository.findAllBySearchTerm("desc2", pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("title2", result.getContent().get(0).getTitle());
    }

    @Test
    void findAllBySearchTerm_shouldReturnPagedResults() {
        Pageable pageable1 = PageRequest.of(0, 1);
        Pageable pageable2 = PageRequest.of(1, 1);

        // Act
        Page<ItemEntity> firstPage = itemRepository.findAllBySearchTerm(null, pageable1);
        Page<ItemEntity> secondPage = itemRepository.findAllBySearchTerm(null, pageable2);

        // Assert
        assertEquals(2, firstPage.getTotalElements());
        assertEquals(1, firstPage.getContent().size());
        assertEquals(1, secondPage.getContent().size());
        assertNotEquals(firstPage.getContent().get(0).getTitle(), secondPage.getContent().get(0).getTitle());
    }

    @Test
    void findAllBySearchTerm_shouldBeCaseInsensitive() {
        // Act
        Page<ItemEntity> result1 = itemRepository.findAllBySearchTerm(title1, pageable);
        Page<ItemEntity> result2 = itemRepository.findAllBySearchTerm(title1.toUpperCase(), pageable);

        // Assert
        assertEquals(1, result1.getTotalElements());
        assertEquals(1, result2.getTotalElements());
    }

    @Test
    void existsByTitle_shouldReturnTrueWhenTitleExists() {
        // Act
        boolean exists = itemRepository.existsByTitle(title1);

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByTitle_shouldReturnFalseWhenTitleDoesNotExist() {
        // Act
        boolean exists = itemRepository.existsByTitle("Non-existent Item");

        // Assert
        assertFalse(exists);
    }

    @Test
    void existsByTitle_shouldBeCaseSensitive() {
        // Act
        boolean existsLowercase = itemRepository.existsByTitle("Title1");
        boolean existsUppercase = itemRepository.existsByTitle("TITLE");

        // Assert
        assertFalse(existsLowercase);
        assertFalse(existsUppercase);
    }
}
