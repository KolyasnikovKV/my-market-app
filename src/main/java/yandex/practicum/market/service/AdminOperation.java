package yandex.practicum.market.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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

    public String addItem(String title, String description, MultipartFile imageFile, BigDecimal price) {
        ItemEntity item = new ItemEntity();
        item.setTitle(title);
        item.setDescription(description);

        // Обработка загрузки файла
        if (imageFile != null && !imageFile.isEmpty()) {
            // Устанавливаем имя файла
            String fileName = imageFile.getOriginalFilename();
            item.setImgPath(fileName);
        }

        item.setPrice(price);

        // Сохраняем товар
        final ItemEntity savedItem = adminService.saveItem(item);

        if (savedItem != null) {
            final Long itemId = savedItem.getId();
            // Сохраняем изображение
            if (imageFile != null && !imageFile.isEmpty())
                storageService.store(itemId.toString(), imageFile);
        }

        Long itemId = savedItem.getId();
        String itemTitle = savedItem.getTitle();
        String message = String.format("Item has been added successfully: id=%d, title=\"%s\"", itemId, itemTitle);
        return message;
    }
}
