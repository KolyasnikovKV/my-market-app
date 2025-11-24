package yandex.practicum.market.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yandex.practicum.market.dto.ItemDto;
import yandex.practicum.market.repository.ItemRepository;
import yandex.practicum.market.types.ActionType;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;

@Service
public class CartService {
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final Map<String, Map<Long, Integer>> cart = new ConcurrentHashMap<>();

    public CartService(ItemRepository itemRepository, ItemService itemService) {
        this.itemRepository = itemRepository;
        this.itemService = itemService;
    }

    public Flux<ItemDto> getItemDtos(String sessionId) {

        Map<Long, Integer> userCart = cart.get(sessionId);

        List<Long> ids = new ArrayList<>(userCart.keySet());
        return itemService.findAllItemsByIds(ids)
                .map(itemDto -> convertItemWithCartCount(itemDto, userCart));

    }


    public Flux<ItemDto> getCart(String sessionId) {
        Map<Long, Integer> userCart = cart.get(sessionId);

        List<Long> ids = new ArrayList<>(userCart.keySet());
        return itemService.findAllItemsByIds(ids)
                .map(itemDto -> convertItemWithCartCount(itemDto, userCart));
    }


    public Mono<Void> changeItemCountInCartByItemId(String sessionId, Long itemId, ActionType action) {
        Map<Long, Integer> userCart = cart.computeIfAbsent(sessionId, k -> new HashMap<>());

        switch (action) {
            case PLUS -> userCart.compute(itemId, (k, v) -> isNull(v) ? 1 : v + 1);
            case MINUS -> userCart.compute(itemId, (k, v) -> (isNull(v) || v == 0) ? 0 : v - 1);
            case DELETE -> userCart.remove(itemId);
        }
        return Mono.empty();
    }

    public Mono<Integer> getItemCountInCartByItemId(String sessionId, Long itemId) {
        Map<Long, Integer> userCart = cart.computeIfAbsent(sessionId, k -> new HashMap<>());
        return Mono.just(userCart.get(itemId));
    }

    public Flux<ItemDto> getAndResetCart(String sessionId) {
        Map<Long, Integer> userCart = cart.get(sessionId);

        Flux<ItemDto> cartItems = Flux.fromStream(
                userCart.entrySet().stream()
                        .map(entry -> ItemDto.builder()
                                .id(entry.getKey())
                                .count(entry.getValue())
                                .build())
        );
        cart.remove(sessionId);
        return cartItems;
    }

    public Mono<BigDecimal> getCartTotalSum(String sessionId) {
        return Flux.fromIterable((cart.get(sessionId).entrySet()))
                .flatMap(userCart -> itemRepository.findById(userCart.getKey())
                        .map(item -> item.getPrice().multiply(BigDecimal.valueOf(userCart.getValue())))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private ItemDto convertItemWithCartCount(ItemDto item, Map<Long, Integer> userCart) {
        item.setCount(userCart.computeIfAbsent(item.getId(), k -> 0));
        return item;
    }
 }
