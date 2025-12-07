package yandex.practicum.market.dto.factory;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import yandex.practicum.market.dto.ItemDto;
import yandex.practicum.market.entity.ItemEntity;

import java.math.BigDecimal;
import java.nio.file.Path;

@Component
public class ItemDtoFactory {

    // Относительный путь к директории с изображениями товаров
    @Getter
    @Setter
    @Value("${storage.images-dir}")
    private Path imagesDir;

    public ItemDto of(
            @NonNull ItemEntity item,
            @NonNull Integer quantity,
            @NonNull BigDecimal price
    ) {
        Long id = item.getId();
        String title = item.getTitle();
        String description = item.getDescription();

        String itemImgPath = item.getImgPath();
        String imgPath = itemImgPath == null ? null : imagesDir.resolve(Long.toString(id)).resolve(itemImgPath).toString();

        return new ItemDto(id, title, description, imgPath, quantity, price);
    }

    public ItemDto of(@NonNull ItemEntity item, @NonNull Integer quantity) {
        return of(item, quantity, item.getPrice());
    }

}
