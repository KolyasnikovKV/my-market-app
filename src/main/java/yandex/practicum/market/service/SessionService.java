package yandex.practicum.market.service;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import yandex.practicum.market.types.ActionType;
import yandex.practicum.market.entity.SessionEntity;
import yandex.practicum.market.entity.CartItemEntity;
import yandex.practicum.market.entity.ItemEntity;
import yandex.practicum.market.repository.CartItemRepository;
import yandex.practicum.market.repository.SessionRepository;

import java.math.BigDecimal;
import java.util.*;

import static yandex.practicum.market.types.ActionType.*;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;
    private final CartItemRepository cartItemRepository;

    public SessionService(
            SessionRepository sessionRepository,
            CartItemRepository cartItemRepository//,
    ) {
        this.sessionRepository = sessionRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public SessionEntity save(@NonNull SessionEntity cart) {
        return sessionRepository.save(cart);
    }

    public void clear(@NonNull SessionEntity cart) {
        cartItemRepository.deleteAllByCartId(cart.getId());
    }

    public void updateCart(
            @NonNull SessionEntity sessionEntity,
            @NonNull ItemEntity item,
            @NonNull ActionType action
    ) {
        Optional<CartItemEntity> cartItemOptional = sessionEntity.getCartItem(item);

        if (cartItemOptional.isPresent()) {
            CartItemEntity cartItem = cartItemOptional.get();

            switch (action) {
                case PLUS -> {
                    Integer quantity = cartItem.getQuantity();
                    quantity++;
                    cartItem.setQuantity(quantity);
                }

                case MINUS -> {
                    Integer quantity = cartItem.getQuantity();
                    quantity--;
                    if (quantity > 0) {
                        cartItem.setQuantity(quantity);
                    } else {
                        sessionEntity.getItems().remove(item);
                    }
                }

                case DELETE -> {
                    sessionEntity.getItems().remove(item);
                }
            }
            sessionRepository.save(sessionEntity);
        } else {
            if (action == PLUS) {
                Integer quantity = 1;
                BigDecimal price = item.getPrice();
                CartItemEntity cartDetail = new CartItemEntity(sessionEntity, item, quantity, price);
                sessionEntity.getItems().put(item, cartDetail);
                sessionRepository.save(sessionEntity);
            }
        }
    }

    public SessionEntity getOrCreateSessionById(@NonNull String sessionId) {
        Optional<SessionEntity> cartOptional = sessionRepository.findBySessionId(sessionId);
        if (cartOptional.isPresent()) {
            return cartOptional.get();
        } else {
            return saveSession(new SessionEntity(sessionId));
        }
    }

    public SessionEntity saveSession(@NonNull SessionEntity sessionEntity) {
        return sessionRepository.save(sessionEntity);
    }

    public BigDecimal getCartTotalCostBySessionId(@NonNull Long sessionId) {
        return cartItemRepository.sumTotalCostInCartBySessionId(sessionId).orElse(BigDecimal.ZERO);
    }
}
