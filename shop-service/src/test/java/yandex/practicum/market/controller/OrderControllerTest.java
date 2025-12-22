package yandex.practicum.market.controller;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yandex.practicum.market.dto.ItemDto;
import yandex.practicum.market.dto.OrderDto;
import yandex.practicum.market.service.OrderService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebFluxTest(OrderController.class)
@Import(OrderService.class)
class OrderControllerTest {


    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OrderService orderService;


  /*  @Test
    @Ignore
    void showOrderTest() {
        Long orderId = 1L;

        OrderDto orderDto = new OrderDto();
        orderDto.setId(orderId);
        orderDto.setTotalCost(new BigDecimal("100.00"));
        ItemDto item1 = ItemDto.builder()
                .id(1L)
                .title("item1")
                .imgPath("img1")
                .price(BigDecimal.valueOf(20))
                .count(2)
                .build();

        ItemDto item2 = ItemDto.builder()
                .id(2L)
                .title("item2")
                .imgPath("img2")
                .price(BigDecimal.valueOf(30))
                .count(2)
                .build();

        orderDto.setItems(List.of(item1, item2));
        when(orderService.findOrderById(anyLong(), anyString())).thenReturn(Mono.just(orderDto));

        webTestClient.get()
                .uri("/orders/{id}?newOrder=true", orderId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String html = response.getResponseBody();
                    assert html != null;
                    assert html.contains("item1");
                    assert html.contains("item2");
                });
    }

    @Test
    @Ignore
    void showOrdersTest() {
        OrderDto order1 = new OrderDto();
        order1.setId(1L);
        order1.setTotalCost(BigDecimal.valueOf(50));

        OrderDto order2 = new OrderDto();
        order2.setId(2L);
        order2.setTotalCost(BigDecimal.valueOf(120));

        ItemDto item1 = ItemDto.builder()
                .id(1L)
                .title("itemA")
                .imgPath("img1")
                .price(BigDecimal.valueOf(20))
                .count(2)
                .build();

        ItemDto item2 = ItemDto.builder()
                .id(2L)
                .title("itemB")
                .imgPath("img2")
                .price(BigDecimal.valueOf(30))
                .count(2)
                .build();

        order1.setItems(List.of(item1, item2));
        order2.setItems(List.of(item1, item2));

        when(orderService.findOrders(anyString())).thenReturn(Flux.just(order1, order2));

        webTestClient.get()
                .uri("/orders")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String html = response.getResponseBody();
                    assert html != null;
                    assert html.contains("itemA");
                    assert html.contains("itemB");
                });
    }*/
}
