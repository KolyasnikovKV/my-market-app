package yandex.practicum.market.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
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
