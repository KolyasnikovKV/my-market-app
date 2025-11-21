package yandex.practicum.market.service;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import yandex.practicum.market.dto.ItemDto;
import yandex.practicum.market.dto.factory.ItemDtoFactory;
import yandex.practicum.market.entity.CartItemEntity;
import yandex.practicum.market.entity.CartEntity;
import yandex.practicum.market.entity.ItemEntity;
import yandex.practicum.market.types.ActionType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CartOperationService {
    private final CartService cartService;
    private final ItemService itemService;
    private final ItemDtoFactory itemDtoFactory;

    public CartOperationService(
            CartService cartService,
            ItemService itemService,
            ItemDtoFactory itemDtoFactory) {
        this.cartService = cartService;
        this.itemService = itemService;
        this.itemDtoFactory = itemDtoFactory;
    }

   public void updateCart(
            @NonNull String sessionId,
            @NonNull Long itemId,
            @NonNull ActionType action) throws NoSuchElementException {
        ItemEntity item = itemService.getItem(itemId);
        CartEntity cartEntity = cartService.getOrCreateSessionById(sessionId);
        cartService.updateCart(cartEntity, item, action);
    }

    public List<ItemDto> getItemDtos(CartEntity cartEntity) {
        Collection<CartItemEntity> cartItemEntities = cartEntity.getItems().values();

        List<ItemDto> items = new ArrayList<>(cartItemEntities.size());
        for(CartItemEntity cartItem : cartItemEntities) {
            ItemEntity item = cartItem.getItem();
            Integer quantity = cartItem.getQuantity();
            ItemDto itemDto = itemDtoFactory.of(item, quantity);
            items.add(itemDto);
        }
        return items;
    }
}
