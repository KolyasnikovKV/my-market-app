package yandex.practicum.market.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import yandex.practicum.market.dto.ItemDto;
import yandex.practicum.market.entity.ItemEntity;
import yandex.practicum.market.entity.OrderItemEntity;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {

    ItemDto toItemDto(ItemEntity itemEntity);

    List<ItemDto> toItemDtos(List<OrderItemEntity> itemEntities);
}
