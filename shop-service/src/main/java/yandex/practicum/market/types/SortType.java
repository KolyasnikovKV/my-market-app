package yandex.practicum.market.types;

import org.springframework.data.domain.Sort;

public enum SortType {
    NO, ALPHA, PRICE;
    public static Sort toSort(SortType sortType) {
        if (sortType == null) {
            return Sort.unsorted();
        }

        switch (sortType) {
            case ALPHA:
                return Sort.by(Sort.Direction.ASC, "title");
            case PRICE:
                return Sort.by(Sort.Direction.ASC, "price");
            case NO:
            default:
                return Sort.unsorted();
        }
    }
}
