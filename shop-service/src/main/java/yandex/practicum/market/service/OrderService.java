package yandex.practicum.market.service;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yandex.practicum.market.client.api.PaymentApi;
import yandex.practicum.market.client.model.PaymentRequest;
import yandex.practicum.market.dto.OrderDto;
import yandex.practicum.market.entity.OrderEntity;
import yandex.practicum.market.entity.OrderItemEntity;
import yandex.practicum.market.exception.PaymentException;
import yandex.practicum.market.mapper.ItemMapper;
import yandex.practicum.market.repository.ItemRepository;
import yandex.practicum.market.repository.OrderItemRepository;
import yandex.practicum.market.repository.OrderRepository;

import java.math.BigDecimal;

@Service
public class OrderService {
    private final ItemMapper itemMapper;
    private final CartService cartService;
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentApi paymentApi;


    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                        ItemMapper itemMapper, CartService cartService, ItemRepository itemRepository,
                        ItemService itemService, PaymentApi paymentApi) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.itemMapper = itemMapper;
        this.cartService = cartService;
        this.itemRepository = itemRepository;
        this.itemService = itemService;
        this.paymentApi = paymentApi;
    }

    @Transactional
    public Mono<Long> createOrder(String sessionId) {
        return cartService.getCartTotalSum(sessionId)
                .flatMap(totalSum ->
                        paymentApi.makePayment(new PaymentRequest().sum(totalSum))
                                .onErrorResume(error -> {
                                    return Mono.error(new PaymentException("PAYMENT_ERROR", error));
                                })
                                .thenMany(cartService.getAndResetCart(sessionId))
                                .collectList()
                                .flatMap(items ->
                                        orderRepository.save(OrderEntity.builder()
                                                        .sessionId(sessionId)
                                                        .build())
                                                .flatMap(order -> {
                                                    Flux<OrderItemEntity> orderItemsFlux = Flux.fromIterable(items)
                                                            .flatMap(item -> itemRepository.findById(item.getId())
                                                                    .map(itemEntity -> OrderItemEntity.builder()
                                                                            .orderId(order.getId())
                                                                            .itemId(itemEntity.getId())
                                                                            .count(item.getCount())
                                                                            .build())
                                                            );
                                                    return orderItemsFlux.collectList()
                                                            .flatMap(orderItemRepository::saveAll)
                                                            .thenMany(Flux.fromIterable(items))
                                                            .then(Mono.just(order.getId()));
                                                })

                                ));
    }

    public Flux<OrderDto> findOrders(String sessionId) {
        return orderRepository.findAllBySessionIdOrderById(sessionId)
                .flatMap(order -> orderItemRepository.findByOrderId(order.getId())
                        .collectList()
                        .map(items -> OrderDto.builder()
                                .id(order.getId())
                                .items(itemMapper.toItemDtos(items))
                                .build())
                );
    }

    public Mono<OrderDto> findOrderById(Long orderId, String sessionId) {

        return orderRepository.findByIdAndSessionId(orderId, sessionId)
                .flatMap(order -> orderItemRepository.findByOrderId(order.getId())
                        .collectList()
                        .map(items -> OrderDto.builder()
                                .id(order.getId())
                                .items(itemMapper.toItemDtos(items))
                                .build())
                )
                .switchIfEmpty(Mono.just(new OrderDto()));
    }

    public Mono<BigDecimal> getOrderTotalCost(@NonNull Long id) {
        return orderItemRepository.sumTotalCostInOrder(id).switchIfEmpty(Mono.just(BigDecimal.ZERO));
    }
}
