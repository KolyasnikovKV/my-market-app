package yandex.practicum.market.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private List<ItemDto> items;
    private BigDecimal totalCost;

    public Long id() {
        return id;
    }

    public List<ItemDto> items() {
        return items;
    }

    public BigDecimal totalSum() {
        return totalCost;
    }
}
