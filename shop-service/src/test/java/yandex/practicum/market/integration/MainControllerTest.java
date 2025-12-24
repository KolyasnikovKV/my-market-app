package yandex.practicum.market.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
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
    @WithMockUser
    void getMainPage_shouldReturnHtmlWithMainTest() {

        itemRepository.save(new ItemEntity(null,"Item1","Description1", "1", BigDecimal.ONE)).then();
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
    @WithMockUser
    void getItemDtoById_Cache() {

        itemRepository.save(new ItemEntity(null,"Item1","Description1", "1", BigDecimal.ONE)).then();

        webTestClient.get()
                .uri("/items/1")
                .exchange()
                .expectStatus().isOk();
    }
}