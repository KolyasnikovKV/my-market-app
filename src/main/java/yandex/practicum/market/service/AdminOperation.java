package yandex.practicum.market.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import yandex.practicum.market.entity.ItemEntity;
import yandex.practicum.market.storage.StorageService;

import java.math.BigDecimal;

@Service
public class AdminOperation {
    private final AdminService adminService;
    private final StorageService storageService;

    public AdminOperation(AdminService adminService,
                          StorageService storageService) {
        this.adminService = adminService;
        this.storageService = storageService;
    }


    public Mono<String> addItem(
            String title,
            String description,
            MultipartFile imageFile,
            BigDecimal price
    ) {
        // Создаём сущность товара
        ItemEntity item = new ItemEntity();
        item.setTitle(title);
        item.setDescription(description);

        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = imageFile.getOriginalFilename();
            item.setImgPath(fileName);
        }
        item.setPrice(price);

        // Сохраняем товар (реактивный вызов)
        return adminService.saveItem(item)
                .flatMap(savedItem -> {
                    Long itemId = savedItem.getId();
                    String itemTitle = savedItem.getTitle();

                    // Если есть файл — сохраняем его (реактивный вызов)
                    if (imageFile != null && !imageFile.isEmpty()) {
                        storageService.store(itemId.toString(), imageFile);
                        return Mono.just(String.format(
                                        "Item has been added successfully: id=%d, title=\"%s\"",
                                        itemId, itemTitle
                                ));
                    } else {
                        // Нет файла — сразу возвращаем сообщение
                        return Mono.just(String.format
                                ("Item has been added successfully: id=%d, title=\"%s\"",
                                        itemId, itemTitle)
                        );
                    }
                })
                .onErrorResume(ex -> {
                    return Mono.error(new RuntimeException("Не удалось добавить товар: " + ex.getMessage()));
                });
    }
}
