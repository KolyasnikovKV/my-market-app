package yandex.practicum.market.service;

import org.springframework.stereotype.Service;
import yandex.practicum.market.dto.factory.ItemDtoFactory;


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

}
