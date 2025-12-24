package yandex.practicum.market.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yandex.practicum.market.dto.ItemDto;
import yandex.practicum.market.dto.factory.ItemDtoFactory;
import yandex.practicum.market.entity.ItemEntity;

import java.util.*;

@Service
public class ItemOperationService {
    @Value("${presentation.item-row-size}")
    @Setter
    @Getter
    private int itemRowSize;

    private final ItemDtoFactory itemDtoFactory;
    private final CartService cartService;
    private final ItemService itemService;
    private final SecurityService securityService;

    public ItemOperationService(ItemDtoFactory itemDtoFactory, CartService cartService, ItemService itemService, SecurityService securityService) {
        this.itemDtoFactory = itemDtoFactory;
        this.cartService = cartService;
        this.itemService = itemService;
        this.securityService = securityService;
    }

    public List<List<ItemDto>> getListOfListItemDto(Long userId, List<ItemEntity> items) {

        List<List<ItemDto>> listOfListItemDto  = new LinkedList<>();

        int count = 0;
        List<ItemDto> listItemDto = new ArrayList<>(itemRowSize);
        listOfListItemDto .add(listItemDto);

        for (ItemEntity item : items) {
            Integer quantity = 0;
            Optional<Integer> cartItemOptional = cartService.getItemCountInCartByItemId(userId, item.getId()).blockOptional();
            if (cartItemOptional.isPresent()) {
                quantity = cartItemOptional.get();
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

    public Mono<ItemDto> getItem(Long id, String sessionId) {
        return itemService.getItem(id) // Возвращает Mono<ItemEntity>
                .zipWith(
                        cartService.getItemCountInCartByItemId(securityService.getCurrentUserId().block(), id),
                        (item, cartCount) -> itemDtoFactory.of(item, cartCount)
                );

    }
}
