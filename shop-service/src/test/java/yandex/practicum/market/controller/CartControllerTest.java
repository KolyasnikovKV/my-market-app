package yandex.practicum.market.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yandex.practicum.market.dto.ItemDto;
import yandex.practicum.market.dto.factory.ItemDtoFactory;
import yandex.practicum.market.entity.ItemEntity;
import yandex.practicum.market.service.CartOperationService;
import yandex.practicum.market.service.CartService;
import yandex.practicum.market.service.ItemService;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import yandex.practicum.market.service.SecurityService;
import yandex.practicum.market.types.ActionType;

@WebFluxTest(CartController.class)
@Import({ItemDtoFactory.class, CartOperationService.class})
public class CartControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private ItemService itemService;

    @MockitoBean
    private SecurityService securityService;

    private final String sessionId = "1";

    private ItemEntity testItem;

    @MockitoBean
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @MockitoBean
    private ReactiveOAuth2AuthorizedClientService authorizedClientService;


    @Test
    @WithMockUser
    void showCart_shouldReturnCartViewWithItems() throws Exception {
        // Подготовка данных корзины
        when(cartService.getBalance()).thenReturn(Mono.just(BigDecimal.valueOf(1)));
        when(cartService.getCartTotalSum()).thenReturn(Mono.just(BigDecimal.valueOf(1)));
        when(cartService.getCart()).thenReturn(Flux.just(new ItemDto(1L, "title1", "desc1", "img1", 1, new BigDecimal(1))));

        webTestClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assertThat(html).contains("title1");
                    assertThat(html).contains("desc1");
                    assertThat(html).contains("img1");
                });
    }

}
