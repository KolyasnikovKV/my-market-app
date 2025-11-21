package yandex.practicum.market.service;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import yandex.practicum.market.types.ActionType;
import yandex.practicum.market.entity.CartEntity;
import yandex.practicum.market.entity.CartItemEntity;
import yandex.practicum.market.entity.ItemEntity;
import yandex.practicum.market.repository.CartItemRepository;
import yandex.practicum.market.repository.CartRepository;

import java.math.BigDecimal;
import java.util.*;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository//,
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public CartEntity save(@NonNull CartEntity cart) {
        return cartRepository.save(cart);
    }

    public void clear(@NonNull CartEntity cart) {
        cartItemRepository.deleteAllByCartId(cart.getId());
    }

    public void updateCart(
            @NonNull CartEntity cartEntity,
            @NonNull ItemEntity item,
            @NonNull ActionType action
    ) {
        Optional<CartItemEntity> cartItemOptional = cartEntity.getCartItem(item);

        if (cartItemOptional.isPresent()) {
            CartItemEntity cartItem = cartItemOptional.get();
            handleAction(cartEntity, item, action, cartItem);
        } else if (action == ActionType.PLUS) {
            addItemToCart(cartEntity, item);
        }

        saveCart(cartEntity);
    }

    private void handleAction(CartEntity cartEntity, ItemEntity item, ActionType action, CartItemEntity cartItem) {
        switch (action) {
            case PLUS -> increaseQuantity(cartItem);
            case MINUS -> decreaseQuantity(cartEntity, item, cartItem);
            case DELETE -> removeItemFromCart(cartEntity, item);
        }
    }

    private void increaseQuantity(CartItemEntity cartItem) {
        int quantity = cartItem.getQuantity();
        cartItem.setQuantity(quantity + 1);
    }

    private void decreaseQuantity(CartEntity cartEntity, ItemEntity item, CartItemEntity cartItem) {
        int quantity = cartItem.getQuantity();
        if (quantity > 1) {
            cartItem.setQuantity(quantity - 1);
        } else {
            removeItemFromCart(cartEntity, item);
        }
    }

    private void removeItemFromCart(CartEntity cartEntity, ItemEntity item) {
        cartEntity.getItems().remove(item);
    }

    private void addItemToCart(CartEntity cartEntity, ItemEntity item) {
        int quantity = 1;
        BigDecimal price = item.getPrice();
        CartItemEntity cartDetail = new CartItemEntity(cartEntity, item, quantity, price);
        cartEntity.getItems().put(item, cartDetail);
    }

    private void saveCart(CartEntity cartEntity) {
        cartRepository.save(cartEntity);
    }

    public CartEntity getOrCreateSessionById(@NonNull String sessionId) {
        Optional<CartEntity> cartOptional = cartRepository.findBySessionId(sessionId);
        if (cartOptional.isPresent()) {
            return cartOptional.get();
        } else {
            return saveSession(new CartEntity(sessionId));
        }
    }

    public CartEntity saveSession(@NonNull CartEntity cartEntity) {
        return cartRepository.save(cartEntity);
    }

    public BigDecimal getCartTotalCostBySessionId(@NonNull Long sessionId) {
        return cartItemRepository.sumTotalCostInCartBySessionId(sessionId).orElse(BigDecimal.ZERO);
    }
}
