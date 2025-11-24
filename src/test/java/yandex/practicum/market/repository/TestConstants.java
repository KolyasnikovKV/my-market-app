package yandex.practicum.market.repository;

import yandex.practicum.market.entity.ItemEntity;

import java.math.BigDecimal;

public class TestConstants {

    public static final Long ITEM_ID = 21L;
    public static final String ITEM_TITLE = "title";
    public static final String ITEM_DESCRIPTION = "description";
    public static final String ITEM_IMAGE_PATH = "image_path";
    public static final Integer ITEM_COUNT = 10;
    public static final BigDecimal ITEM_PRICE = BigDecimal.valueOf(1.50);

    public static final ItemEntity ITEM_ENTITY = ItemEntity.builder()
            .id(ITEM_ID)
            .title(ITEM_TITLE)
            .description(ITEM_DESCRIPTION)
            .imgPath(ITEM_IMAGE_PATH)
            .price(ITEM_PRICE)
            .build();
}
