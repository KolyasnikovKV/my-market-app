package yandex.practicum.market.service;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import yandex.practicum.market.entity.CartEntity;
import yandex.practicum.market.entity.OrderEntity;
import yandex.practicum.market.repository.OrderItemRepository;
import yandex.practicum.market.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public OrderEntity buy(@NonNull CartEntity cartEntity) {
        OrderEntity order = new OrderEntity(cartEntity);
        OrderEntity savedOrder = orderRepository.save(order);

        return savedOrder;
    }

    public List<OrderEntity> getAllOrdersBySessionId(@NonNull Long id) {
        return orderRepository.findBySessionId(id);
    }

    public OrderEntity getOrder(@NonNull Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Invalid order"));
    }

    public BigDecimal getOrderTotalCost(@NonNull Long id) {
        return orderItemRepository.sumTotalCostInOrder(id).orElse(BigDecimal.ZERO);
    }
}
