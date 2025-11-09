package yandex.practicum.market.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    private String imgPath;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    public ItemEntity(String title, String description, String imgPath, BigDecimal price) {
        this.title = title;
        this.description = description;
        this.imgPath = imgPath;
        this.price = price;
    }
}