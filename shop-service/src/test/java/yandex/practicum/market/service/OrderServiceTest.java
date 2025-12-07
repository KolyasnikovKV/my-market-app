package yandex.practicum.market.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import yandex.practicum.market.dto.ItemDto;
import yandex.practicum.market.dto.OrderDto;
import yandex.practicum.market.entity.OrderEntity;
import yandex.practicum.market.entity.OrderItemEntity;
import yandex.practicum.market.repository.OrderItemRepository;
import yandex.practicum.market.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CartService cartService;


    @Mock
    private WebSession session;

    private static final String USERNAME = "john";
    private static final UUID USER_ID = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");


    @Test
    void createOrderTest() {
        // given
        ItemDto item1 = ItemDto.builder()
                .id(1L).title("Item 1").price(BigDecimal.valueOf(10)).count(2).build();
        ItemDto item2 = ItemDto.builder()
                .id(2L).title("Item 2").price(BigDecimal.valueOf(20)).count(1).build();
        List<ItemDto> items = List.of(item1, item2);
        BigDecimal totalSum = BigDecimal.valueOf(40);

        OrderEntity savedOrder = OrderEntity.builder().id(1L).build();
        OrderItemEntity item1Entity = OrderItemEntity.builder().orderId(savedOrder.getId()).itemId(10L).build();
        OrderItemEntity item2Entity = OrderItemEntity.builder().orderId(savedOrder.getId()).itemId(11L).build();

        // mocks
        when(orderRepository.save(any(OrderEntity.class)))
                .thenReturn(Mono.just(savedOrder));

        when(orderItemRepository.saveAll(anyList()))
                .thenReturn(Mono.empty());

        // when
        Mono<Long> result = orderService.createOrder("session");

        // then
        StepVerifier.create(result)
                .expectNextMatches(order ->
                        order.equals(1L))
                .verifyComplete();

        verify(orderRepository).save(any(OrderEntity.class));
        verify(orderItemRepository).saveAll(anyList());
    }

    @Test
    void findById_shouldReturnOrder_whenFound() {
        // given
        OrderEntity order = OrderEntity.builder()
                .id(1L)
                .build();

        // mocks
        when(orderRepository.findById(anyLong())).thenReturn(Mono.just(order));

        // when
        Mono<OrderDto> result = orderService.findOrderById(order.getId(), "session");

        // then
        StepVerifier.create(result)
                .expectNextMatches(res -> res.getId().equals(order.getId()))
                .verifyComplete();
    }

    @Test
    void getOrderWithItems() {
        // given
        OrderEntity order1 = OrderEntity.builder().id(1L).build();
        OrderEntity order2 = OrderEntity.builder().id(2L).build();

        OrderItemEntity item1 = OrderItemEntity.builder().orderId(1L).itemId(10L).build();
        OrderItemEntity item2 = OrderItemEntity.builder().orderId(1L).itemId(11L).build();
        OrderItemEntity item3 = OrderItemEntity.builder().orderId(2L).itemId(20L).build();

        when(orderItemRepository.findByOrderId(1L))
                .thenReturn(Flux.just(item1, item2));

        when(orderItemRepository.findByOrderId(2L))
                .thenReturn(Flux.just(item3));

        when(orderRepository.findAllBySessionIdOrderById(anyString())).
                thenReturn(Flux.just(order1, order2));

        // when
        Flux<OrderDto> result = orderService.findOrders("session");

        // then
        StepVerifier.create(result)
                .assertNext(dto -> {
                    assert dto.getId() == 1;
                })
                .assertNext(dto -> {
                    assert dto.getId() == 2;
                })
                .verifyComplete();
    }
}
