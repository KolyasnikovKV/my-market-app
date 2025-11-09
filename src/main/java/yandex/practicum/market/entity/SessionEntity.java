package yandex.practicum.market.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.*;

@Entity
@Table(name = "sessions")
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SessionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", unique = true, nullable = false, length = 64)
    private String sessionId;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKey(name = "item")
    private Map<ItemEntity, CartItemEntity> details = new HashMap<>();

    public SessionEntity(String sessionId) {
        this.sessionId = sessionId;
    }

    public SessionEntity(Long id, String sessionId) {
        this.id = id;
        this.sessionId = sessionId;
    }

    public Optional<CartItemEntity> getCartDetail(@NonNull ItemEntity item) {
        CartItemEntity cartDetail = details.get(item);

        return Optional.ofNullable(cartDetail);
    }

}