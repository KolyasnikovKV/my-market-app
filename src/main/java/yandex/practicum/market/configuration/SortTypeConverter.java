package yandex.practicum.market.configuration;

import org.springframework.core.convert.converter.Converter;
import yandex.practicum.market.types.SortType;

public class SortTypeConverter implements Converter<String, SortType> {
    @Override
    public SortType convert(String source) {
        return SortType.valueOf(source.toUpperCase());
    }
}

