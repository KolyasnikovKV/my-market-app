package yandex.practicum.market.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.*;

@Entity
@Table(name = "carts")
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class CartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", unique = true, nullable = false, length = 64)
    private String sessionId;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKey(name = "item")
    private Map<ItemEntity, CartItemEntity> items = new HashMap<>();

    public CartEntity(String sessionId) {
        this.sessionId = sessionId;
    }

    public CartEntity(Long id, String sessionId) {
        this.id = id;
        this.sessionId = sessionId;
    }

    public Optional<CartItemEntity> getCartItem(@NonNull ItemEntity item) {
        CartItemEntity cartDetail = items.get(item);

        return Optional.ofNullable(cartDetail);
    }

}