package yandex.practicum.market.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


import java.math.BigDecimal;

@Table(name = "order_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"orderId", "itemId"})
public class OrderItemEntity {
    @Id
    private Long id;

    private Long orderId;

    private Long itemId;

    private Integer count;

    private BigDecimal price;
}
