package yandex.practicum.market.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import yandex.practicum.market.BaseIntegrationTest;
import yandex.practicum.market.entity.ItemEntity;
import yandex.practicum.market.repository.ItemRepository;

import java.math.BigDecimal;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainControllerTest extends BaseIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void getMainPage_shouldReturnHtmlWithMainTest() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/items")
                        .queryParam("search", EMPTY)
                        .queryParam("sort", "NO")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("<form"));
                });
    }

    @Test
    void getItemDtoById_Cache() {

        itemRepository.save(new ItemEntity(3L,"","", "", BigDecimal.ONE));

        webTestClient.get()
                .uri("/items/3")
                .exchange()
                .expectStatus().isOk();
    }
}