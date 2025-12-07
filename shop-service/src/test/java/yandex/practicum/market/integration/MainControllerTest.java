package yandex.practicum.market.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import yandex.practicum.market.BaseIntegrationTest;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainControllerTest extends BaseIntegrationTest {

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
}