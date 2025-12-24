package yandex.practicum.market.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import yandex.practicum.market.client.api.PaymentApi;
import yandex.practicum.market.dto.ItemDto;
import yandex.practicum.market.dto.OrderDto;
import yandex.practicum.market.entity.ItemEntity;
import yandex.practicum.market.entity.OrderEntity;
import yandex.practicum.market.entity.OrderItemEntity;
import yandex.practicum.market.mapper.ItemMapper;
import yandex.practicum.market.mapper.ItemMapperImpl;
import yandex.practicum.market.repository.ItemRepository;
import yandex.practicum.market.repository.OrderItemRepository;
import yandex.practicum.market.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Import({ItemService.class, ItemMapper.class})
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderServiceTest {

    @InjectMocks
    @Spy
    private OrderService orderService;

    @Mock
    private ItemMapper itemMapper = new ItemMapperImpl();

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CartService cartService;

    @Mock
    private PaymentApi paymentApi;

    @Mock
    private SecurityService securityService;

    @Test
    @WithMockUser
    void createOrderTest() {
        // given
        ItemDto item1 = ItemDto.builder()
                .id(1L).title("Item 1").price(BigDecimal.valueOf(10)).count(2).build();
        ItemDto item2 = ItemDto.builder()
                .id(2L).title("Item 2").price(BigDecimal.valueOf(20)).count(1).build();
        List<ItemDto> items = List.of(item1, item2);
        BigDecimal totalSum = BigDecimal.valueOf(40);

        ItemEntity itemEntity1 = ItemEntity.builder().id(1L).title("Item 1").build();
        ItemEntity itemEntity2 = ItemEntity.builder().id(2L).title("Item 2").build();

        OrderEntity savedOrder = OrderEntity.builder().id(1L).build();
        OrderItemEntity item1Entity = OrderItemEntity.builder().orderId(savedOrder.getId()).itemId(10L).build();
        OrderItemEntity item2Entity = OrderItemEntity.builder().orderId(savedOrder.getId()).itemId(11L).build();

        // mocks
        when(paymentApi.makePayment(any())).thenReturn(Mono.empty());
        when(cartService.getCartTotalSum()).thenReturn(Mono.just(BigDecimal.TEN));
        when(cartService.getAndResetCart()).thenReturn(Flux.just(item1, item2));
        when(orderRepository.save(any(OrderEntity.class)))
                .thenReturn(Mono.just(savedOrder));
        when(orderItemRepository.saveAll(anyList()))
                .thenReturn(Mono.empty());
        when(itemRepository.findById(1L)).thenReturn(Mono.just(itemEntity1));
        when(itemRepository.findById(2L)).thenReturn(Mono.just(itemEntity2));
        when(securityService.getCurrentUserId()).thenReturn(Mono.just(1L));

        // when
        Mono<Long> result = orderService.createOrder();

        // then
        StepVerifier.create(result)
                .expectNextMatches(order ->
                        order.equals(1L))
                .verifyComplete();

        verify(orderRepository).save(any(OrderEntity.class));
        verify(orderItemRepository).saveAll(anyList());
    }

}
