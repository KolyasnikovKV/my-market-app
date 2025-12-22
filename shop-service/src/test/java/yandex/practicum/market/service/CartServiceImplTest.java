package yandex.practicum.market.service;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.annotation.Import;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import yandex.practicum.market.dto.ItemDto;
import yandex.practicum.market.entity.ItemEntity;
import yandex.practicum.market.mapper.ItemMapper;
import yandex.practicum.market.mapper.ItemMapperImpl;
import yandex.practicum.market.repository.ItemRepository;
import yandex.practicum.market.types.ActionType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Import({ItemService.class,ItemMapper.class})
@MockitoSettings(strictness = Strictness.LENIENT)
class CartServiceImplTest {

    @Spy
    @InjectMocks
    private CartService cartService;

    @InjectMocks
    private ItemMapper itemMapper = new ItemMapperImpl();

    @Mock
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private WebSession session;

    private static final String USERNAME = "john";
    private static final String PRINCIPAL_KEY = "PRINCIPAL";
    private static String cartKey(String username) {
        return "CART:" + username;
    }

    @Test
    void getCartItemsTest() {
        // given
        Map<String, Object> attributes = new HashMap<>();
        Map<Long, Integer> cart = new LinkedHashMap<>(); // чтобы был предсказуемый порядок
        cart.put(1L, 2);
        cart.put(2L, 1);
        attributes.put(PRINCIPAL_KEY, USERNAME);
        attributes.put(cartKey(USERNAME), cart);

        ItemEntity item1 = ItemEntity.builder().id(1L).title("Item 1").build();
        ItemEntity item2 = ItemEntity.builder().id(2L).title("Item 2").build();

        cartService.changeItemCountInCartByItemId("1", 1L, ActionType.PLUS);
        cartService.changeItemCountInCartByItemId("1", 2L, ActionType.PLUS);

        // mock
        when(session.getAttributes()).thenReturn(attributes);
        when(itemRepository.findById(1L)).thenReturn(Mono.just(item1));
        when(itemRepository.findById(2L)).thenReturn(Mono.just(item2));
        when(itemRepository.findAllById(anyList())).thenReturn(Flux.just(item1, item2));
        when(itemService.findAllItemsByIds(any())).thenReturn(Flux.just(itemMapper.toItemDto(item1), itemMapper.toItemDto(item2)));

        // when
        Mono<List<ItemDto>> result = cartService.getCart("1").collectList();

        // then
        StepVerifier.create(result)
                .assertNext(items -> {
                    // не завязываемся жёстко на порядок, но для LinkedHashMap всё равно будет [1,2]
                    var ids = items.stream().map(ItemDto::getId).collect(Collectors.toList());
                    assert items.size() == 2;
                    assert ids.containsAll(List.of(1L, 2L));
                })
                .verifyComplete();
    }

    @Test
    void getTotalPriceTest() {
        // given
        cartService.changeItemCountInCartByItemId("1", 1L, ActionType.PLUS);
        cartService.changeItemCountInCartByItemId("1", 2L, ActionType.PLUS);

        ItemEntity item1 = ItemEntity.builder().id(1L).price(BigDecimal.valueOf(10)).build(); // 2 * 10
        ItemEntity item2 = ItemEntity.builder().id(2L).price(BigDecimal.valueOf(30)).build(); // 1 * 30

        // mock
        when(itemRepository.findById(1L)).thenReturn(Mono.just(item1));
        when(itemRepository.findById(2L)).thenReturn(Mono.just(item2));
        when(itemRepository.findById(1L)).thenReturn(Mono.just(item1));
        when(itemRepository.findById(2L)).thenReturn(Mono.just(item2));
        when(itemRepository.findAllById(anyList())).thenReturn(Flux.just(item1, item2));
        when(itemService.findAllItemsByIds(any())).thenReturn(Flux.just(itemMapper.toItemDto(item1), itemMapper.toItemDto(item2)));

        // when
        Mono<BigDecimal> result = cartService.getCartTotalSum("1");

        // then
        StepVerifier.create(result)
                .expectNext(BigDecimal.valueOf(40))
                .verifyComplete();
    }
}

