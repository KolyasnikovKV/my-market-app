package yandex.practicum.market.configuration;

import org.springframework.core.convert.converter.Converter;
import yandex.practicum.market.types.ActionType;

public class ActionTypeConverter implements Converter<String, ActionType> {
    @Override
    public ActionType convert(String source) {
        return ActionType.valueOf(source.toUpperCase());
    }
}

