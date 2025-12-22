package yandex.practicum.market.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table(name = "items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ItemEntity {
    @Id
    private Long id;

    private String title;

    private String description;

    private String imgPath;

    private BigDecimal price;
}