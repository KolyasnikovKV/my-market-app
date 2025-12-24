package yandex.practicum.market.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yandex.practicum.market.client.api.PaymentApi;
import yandex.practicum.market.dto.ItemDto;
import yandex.practicum.market.repository.ItemRepository;
import yandex.practicum.market.types.ActionType;
import yandex.practicum.market.client.model.BalanceResponse;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;

@Service
@Slf4j
public class CartService {
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final Map<Long, Map<Long, Integer>> cart = new ConcurrentHashMap<>();
    private final PaymentApi paymentApi;
    private final OAuth2Service oAuth2Service;
    private final SecurityService securityService;

    public CartService(ItemRepository itemRepository, ItemService itemService, PaymentApi paymentApi, OAuth2Service oAuth2Service, SecurityService securityService) {
        this.itemRepository = itemRepository;
        this.itemService = itemService;
        this.paymentApi = paymentApi;
        this.oAuth2Service = oAuth2Service;
        this.securityService = securityService;
    }

  /*  public Flux<ItemDto> getItemDtos(String sessionId) {

        Map<Long, Integer> userCart = cart.get(sessionId);

        List<Long> ids = new ArrayList<>(userCart.keySet());
        return itemService.findAllItemsByIds(ids)
                .map(itemDto -> convertItemWithCartCount(itemDto, userCart));

    }*/


    public Flux<ItemDto> getCart() {
        return securityService.getCurrentUserId()
                .flatMapMany(userId -> {
                    Map<Long, Integer> userCart = cart.get(userId);
                    List<Long> ids = new ArrayList<>(userCart.keySet());
                    return itemService.findAllItemsByIds(ids)
                            .map(itemDto -> convertItemWithCartCount(itemDto, userCart));
                });
    }


    public Mono<Void> changeItemCountInCartByItemId(Long itemId, ActionType action) {
        return securityService.getCurrentUserId()
                .flatMap(userId -> {
                    Map<Long, Integer> userCart = cart.computeIfAbsent(userId, k -> new HashMap<>());

                    switch (action) {
                        case PLUS -> userCart.compute(itemId, (k, v) -> isNull(v) ? 1 : v + 1);
                        case MINUS -> userCart.compute(itemId, (k, v) -> (isNull(v) || v == 0) ? 0 : v - 1);
                        case DELETE -> userCart.remove(itemId);
                    }
                    return Mono.empty();
                });
    }

    public Mono<Integer> getItemCountInCartByItemId(Long userId, Long itemId) {
        Map<Long, Integer> userCart = cart.computeIfAbsent(userId, k -> new HashMap<>());
        return Mono.just(userCart.getOrDefault(itemId, 0));
    }

    public Flux<ItemDto> getAndResetCart() {
        return securityService.getCurrentUserId()
                .flatMapMany(userId -> {
                    Map<Long, Integer> userCart = cart.get(userId);

                    Flux<ItemDto> cartItems = Flux.fromStream(
                            userCart.entrySet().stream()
                                    .map(entry -> ItemDto.builder()
                                            .id(entry.getKey())
                                            .count(entry.getValue())
                                            .build())
                    );
                    cart.remove(userId);
                    return cartItems;
                })
                .switchIfEmpty(Flux.empty());
    }

    public Mono<BigDecimal> getCartTotalSum() {
        return securityService.getCurrentUserId()
                .flatMapMany(userId -> Flux.fromIterable((cart.get(userId).entrySet()))
                        .flatMap(userCart -> itemRepository.findById(userCart.getKey())
                                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(userCart.getValue())))
                        ))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Mono<BigDecimal> getBalance() {
        return oAuth2Service
                .getTokenValue()
                .flatMap(accessToken -> {
                    paymentApi.getApiClient().addDefaultHeader("Authorization", "Bearer " + accessToken);
                    return paymentApi.getBalance();
                })
                .map(BalanceResponse::getBalance)
                .onErrorResume(error -> {
                    log.error("Ошибка при обращении в платежный сервис: {}", error.getMessage(), error);
                    return Mono.just(BigDecimal.ONE.negate());
                });
    }

    private ItemDto convertItemWithCartCount(ItemDto item, Map<Long, Integer> userCart) {
        item.setCount(userCart.computeIfAbsent(item.getId(), k -> 0));
        return item;
    }
 }
