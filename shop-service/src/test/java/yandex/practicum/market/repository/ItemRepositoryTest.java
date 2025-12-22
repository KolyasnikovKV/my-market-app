package yandex.practicum.market.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import yandex.practicum.market.PostreSqlTestcontainer;
import yandex.practicum.market.entity.ItemEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
@Testcontainers
@ImportTestcontainers(PostreSqlTestcontainer.class)
class ItemRepositoryTest {

    private ItemEntity itemEntity;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    public void init() {
        itemRepository.deleteAll()
                .block();

        itemEntity = ItemEntity.builder()
                .title(TestConstants.ITEM_TITLE)
                .description(TestConstants.ITEM_DESCRIPTION)
                .imgPath(TestConstants.ITEM_IMAGE_PATH)
                .price(TestConstants.ITEM_PRICE)
                .build();
    }

    @Test
    void searchAllPagingAndSortingSuccessfulTest() {
        int pageSize = 10;
        String sortColumn = "id";

        Flux<ItemEntity> foundItems = itemRepository.save(itemEntity)
                .thenMany(itemRepository.findAllBySearchTerm(TestConstants.ITEM_TITLE, sortColumn, pageSize, 0));

        StepVerifier.create(foundItems)
                .expectNextMatches(item ->
                        item.getTitle().equals(TestConstants.ITEM_TITLE)
                                && item.getDescription().equals(TestConstants.ITEM_DESCRIPTION)
                                && item.getImgPath().equals(TestConstants.ITEM_IMAGE_PATH)
                                && (item.getPrice().compareTo(TestConstants.ITEM_PRICE) == 0))
                .verifyComplete();
    }
}
