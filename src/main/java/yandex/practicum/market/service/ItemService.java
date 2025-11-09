package yandex.practicum.market.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import yandex.practicum.market.entity.ItemEntity;
import yandex.practicum.market.repository.ItemRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Page<ItemEntity> getItems(@Nullable String searchTerm, @NonNull Pageable pageable) {
        return itemRepository.findAllBySearchTerm(searchTerm, pageable);
    }

    public ItemEntity getItem(@NonNull Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NoSuchElementException("Invalid item"));
    }

}
