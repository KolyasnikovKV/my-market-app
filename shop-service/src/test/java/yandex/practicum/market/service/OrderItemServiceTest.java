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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import yandex.practicum.market.dto.OrderDto;
import yandex.practicum.market.entity.ItemEntity;
import yandex.practicum.market.entity.OrderEntity;
import yandex.practicum.market.entity.OrderItemEntity;
import yandex.practicum.market.mapper.ItemMapper;
import yandex.practicum.market.mapper.ItemMapperImpl;
import yandex.practicum.market.repository.OrderItemRepository;
import yandex.practicum.market.repository.OrderRepository;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Import({ItemService.class,ItemMapper.class})
@MockitoSettings(strictness = Strictness.LENIENT)
public class OrderItemServiceTest {

    @InjectMocks
    @Spy
    private OrderService orderService;

    @Mock
    private ItemMapper itemMapper = new ItemMapperImpl();

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private SecurityService securityService;

    @Test
    @WithMockUser
    void findAllByOrderIdTest() {
        //given
        OrderEntity order = OrderEntity.builder().id(1L).build();
        OrderItemEntity item1 = OrderItemEntity.builder().orderId(order.getId()).itemId(10L).build();
        OrderItemEntity item2 = OrderItemEntity.builder().orderId(order.getId()).itemId(11L).build();

        //mock
        when(orderItemRepository.findAll()).thenReturn(Flux.just(item1, item2));
        when(orderRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Mono.just(order));
        when(orderItemRepository.findByOrderId(anyLong())).thenReturn(Flux.just(item1, item2));
        when(securityService.getCurrentUserId()).thenReturn(Mono.just(1L));

        //when
        Mono<OrderDto> result = orderService.findOrderById(order.getId());

        //then
        StepVerifier.create(result)
                .assertNext(orderDto -> {orderDto.getItems().contains(item1);
                    orderDto.getItems().contains(item2);})
                .verifyComplete();

        verify(orderItemRepository).findByOrderId(order.getId());
    }

}
