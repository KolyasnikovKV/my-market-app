package yandex.practicum.market.service;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import yandex.practicum.market.entity.ItemEntity;
import yandex.practicum.market.repository.ItemRepository;

@Service
public class AdminService {
    private final ItemRepository itemRepository;

    public AdminService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public ItemEntity saveItem(@NonNull ItemEntity item) {
        return itemRepository.save(item);
    }
}
