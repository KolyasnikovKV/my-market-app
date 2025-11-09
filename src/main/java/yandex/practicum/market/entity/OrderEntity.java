package yandex.practicum.market.entity;

import lombok.*;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "session_id", nullable = false)
    private SessionEntity session;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKey(name = "item")
    private Map<ItemEntity, OrderItemEntity> details = new HashMap<>();

    public OrderEntity(@NonNull SessionEntity sessionEntity) {
        this.session = sessionEntity;

        Collection<CartItemEntity> cartDetails = sessionEntity.getDetails().values();

        for (CartItemEntity cartDetail : cartDetails) {
            ItemEntity item = cartDetail.getItem();
            Integer quantity = cartDetail.getQuantity();
            BigDecimal price = cartDetail.getPrice();

            OrderItemEntity orderDetail = new OrderItemEntity(this, item, quantity, price);
            details.put(item, orderDetail);
        }
    }
}