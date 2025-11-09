package yandex.practicum.market.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import yandex.practicum.market.dto.ItemDto;
import yandex.practicum.market.dto.factory.ItemDtoFactory;
import yandex.practicum.market.entity.CartItemEntity;
import yandex.practicum.market.entity.SessionEntity;
import yandex.practicum.market.entity.ItemEntity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class ItemOperationService {
    @Value("${presentation.item-row-size}")
    @Setter
    @Getter
    private int itemRowSize;

    private final ItemDtoFactory itemDtoFactory;
    private final SessionService sessionService;
    private final ItemService itemService;

    public ItemOperationService(ItemDtoFactory itemDtoFactory, SessionService sessionService, ItemService itemService) {
        this.itemDtoFactory = itemDtoFactory;
        this.sessionService = sessionService;
        this.itemService = itemService;
    }

    public List<List<ItemDto>> getListOfListItemDto(String sessionId, Page<ItemEntity> page) {
        SessionEntity cart = sessionService.getOrCreateSessionById(sessionId);

        List<ItemEntity> items = page.getContent();
        List<List<ItemDto>> listOfListItemDto  = new LinkedList<>();

        int count = 0;
        List<ItemDto> listItemDto = new ArrayList<>(itemRowSize);
        listOfListItemDto .add(listItemDto);

        for (ItemEntity item : items) {
            Integer quantity = 0;
            Optional<CartItemEntity> cartItemOptional = cart.getCartItem(item);
            if (cartItemOptional.isPresent()) {
                CartItemEntity cartItem = cartItemOptional.get();
                quantity = cartItem.getQuantity();
            }
            ItemDto itemDto = itemDtoFactory.of(item, quantity);
            listItemDto.add(itemDto);

            if (count < itemRowSize - 1) {
                count++;
            } else {
                count = 0;
                listItemDto = new ArrayList<>(itemRowSize);
                listOfListItemDto.add(listItemDto);
            }
        }
        return listOfListItemDto;
    }
    public ItemDto getItem(Long id, String sessionId) {
        ItemEntity item = itemService.getItem(id);
        SessionEntity sessionEntity = sessionService.getOrCreateSessionById(sessionId);
        Optional<CartItemEntity> cartItemOptional = sessionEntity.getCartItem(item);

        Integer quantity = 0;
        if (cartItemOptional.isPresent()) {
            CartItemEntity cartItem = cartItemOptional.get();
            quantity = cartItem.getQuantity();
        }

        ItemDto itemDto = itemDtoFactory.of(item, quantity);
        return itemDto;
    }
}
