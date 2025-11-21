package yandex.practicum.market.dto.factory;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import yandex.practicum.market.dto.ItemDto;
import yandex.practicum.market.dto.OrderDto;
import yandex.practicum.market.entity.ItemEntity;
import yandex.practicum.market.entity.OrderEntity;
import yandex.practicum.market.entity.OrderItemEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class OrderDtoFactory {

    private final ItemDtoFactory itemDtoFactory;

    public OrderDtoFactory(ItemDtoFactory itemDtoFactory) {
        this.itemDtoFactory = itemDtoFactory;
    }

    public OrderDto of(@NonNull OrderEntity order, BigDecimal totalCost) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());

        Collection<OrderItemEntity> orderDetails = order.getItems().values();
        List<ItemDto> items = new ArrayList<>(orderDetails.size());

        for (OrderItemEntity orderDetail : orderDetails) {
            ItemEntity item = orderDetail.getItem();
            Integer quantity = orderDetail.getQuantity();
            BigDecimal price = orderDetail.getPrice();
            ItemDto itemDto = itemDtoFactory.of(item, quantity, price);
            items.add(itemDto);
        }

        orderDto.setItems(items);

        // Подсчет итоговой суммы заказа
        orderDto.setTotalCost(totalCost);

        return orderDto;
    }
}
