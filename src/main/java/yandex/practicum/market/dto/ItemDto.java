package yandex.practicum.market.dto;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
public class ItemDto {
    private Long id;
    private String title;
    private String description;
    private String imgPath;
    private Integer count;
    private BigDecimal price;
}
