package yandex.practicum.market.service;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import yandex.practicum.market.dto.ItemDto;
import yandex.practicum.market.dto.factory.ItemDtoFactory;
import yandex.practicum.market.entity.CartItemEntity;
import yandex.practicum.market.entity.SessionEntity;
import yandex.practicum.market.entity.ItemEntity;
import yandex.practicum.market.types.ActionType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CartOperationService {
    private final SessionService sessionService;
    private final ItemService itemService;
    private final ItemDtoFactory itemDtoFactory;

    public CartOperationService(
            SessionService sessionService,
            ItemService itemService,
            ItemDtoFactory itemDtoFactory) {
        this.sessionService = sessionService;
        this.itemService = itemService;
        this.itemDtoFactory = itemDtoFactory;
    }

   public void updateCart(
            @NonNull String sessionId,
            @NonNull Long itemId,
            @NonNull ActionType action) throws NoSuchElementException {
        ItemEntity item = itemService.getItem(itemId);
        SessionEntity sessionEntity = sessionService.getOrCreateSessionById(sessionId);
        sessionService.updateCart(sessionEntity, item, action);
    }

    public List<ItemDto> getItemDtos(SessionEntity sessionEntity) {
        Collection<CartItemEntity> cartDetails = sessionEntity.getDetails().values();

        List<ItemDto> items = new ArrayList<>(cartDetails.size());
        for(CartItemEntity cartDetail : cartDetails) {
            ItemEntity item = cartDetail.getItem();
            Integer quantity = cartDetail.getQuantity();
            ItemDto itemDto = itemDtoFactory.of(item, quantity);
            items.add(itemDto);
        }
        return items;
    }
}
