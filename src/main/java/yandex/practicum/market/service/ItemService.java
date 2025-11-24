package yandex.practicum.market.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yandex.practicum.market.dto.ItemDto;
import yandex.practicum.market.entity.ItemEntity;
import yandex.practicum.market.mapper.ItemMapper;
import yandex.practicum.market.repository.ItemRepository;
import yandex.practicum.market.types.SortType;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static yandex.practicum.market.types.SortType.NO;

@Service
public class ItemService {
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository,
                       ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
    }

    public Flux<ItemEntity> getItems(@Nullable String searchTerm, SortType itemSort, Integer pageSize, Integer pageNumber) {
        int offset = Math.max(0, (pageNumber - 1) * pageSize);
        String sortColumn = resolveSortColumn(itemSort);
        return itemRepository.findAllBySearchTerm(searchTerm, sortColumn, pageSize, offset);
    }

    public Mono<ItemEntity> getItem(@NonNull Long itemId) {
        return itemRepository.findById(itemId).switchIfEmpty(Mono.error(new NoSuchElementException("Invalid item")));
    }

     public Flux<ItemDto> findAllItemsByIds(List<Long> itemIds) {
        return itemRepository.findAllById(itemIds)
                .map(itemMapper::toItemDto);
    }

    private String resolveSortColumn(SortType itemSort) {
        return switch (itemSort) {
            case NO -> "id";
            case ALPHA -> "title";
            case PRICE -> "price";
        };
    }

}
