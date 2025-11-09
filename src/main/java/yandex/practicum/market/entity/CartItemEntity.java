package yandex.practicum.market.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_details")
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class CartItemEntity {
    @EmbeddedId
    private CartItemlIdEntity id;

    @ManyToOne
    @MapsId("SessionId")
    @JoinColumn(name = "session_id", nullable = false)
    private SessionEntity session;

    @ManyToOne
    @MapsId("itemId")
    @JoinColumn(name = "item_id", nullable = false)
    private ItemEntity item;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    public CartItemEntity(@NonNull SessionEntity session, @NonNull ItemEntity item, @NonNull Integer quantity, @NonNull BigDecimal price) {
        this.id = new CartItemlIdEntity(session.getId(), item.getId());
        this.session = session;
        this.item = item;
        this.quantity = quantity;
        this.price = price;
    }
}