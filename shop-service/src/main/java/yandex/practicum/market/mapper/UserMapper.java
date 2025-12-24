package yandex.practicum.market.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import yandex.practicum.market.dto.UserDto;
import yandex.practicum.market.entity.UserEntity;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserDto toUserDto(UserEntity userEntity);
}
