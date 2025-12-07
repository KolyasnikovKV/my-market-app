package yandex.practicum.market.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import yandex.practicum.market.dto.OrderDto;
import yandex.practicum.market.entity.ItemEntity;
import yandex.practicum.market.entity.OrderEntity;
import yandex.practicum.market.entity.OrderItemEntity;
import yandex.practicum.market.repository.OrderItemRepository;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderItemServiceTest {

    @InjectMocks
    private OrderService orderItemService;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Test
    void findAllByOrderIdTest() {
        //given
        OrderEntity order = OrderEntity.builder().id(1L).build();
        OrderItemEntity item1 = OrderItemEntity.builder().orderId(order.getId()).itemId(10L).build();
        OrderItemEntity item2 = OrderItemEntity.builder().orderId(order.getId()).itemId(11L).build();

        //mock
        when(orderItemRepository.findAll()).thenReturn(Flux.just(item1, item2));
        //when
        Mono<OrderDto> result = orderItemService.findOrderById(order.getId(), "");
        //then
        StepVerifier.create(result)
                .assertNext(orderDto -> {orderDto.getItems().contains(item1);
                    orderDto.getItems().contains(item2);})
                .verifyComplete();

        verify(orderItemRepository).findByOrderId(order.getId());
    }

}
