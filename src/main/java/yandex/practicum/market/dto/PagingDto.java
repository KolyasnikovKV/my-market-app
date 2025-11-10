package yandex.practicum.market.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.domain.Page;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PagingDto {
    private int oneBasedPageNumber;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;

    public int pageNumber() {
        return oneBasedPageNumber;
    }

    public int pageSize() {
        return pageSize;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public boolean hasPrevious() {
        return hasPrevious;
    }

    public static PagingDto of(@NonNull Page<?> page) {
        int zeroBasedPageNumber = page.getNumber();
        int oneBasedPageNumber = zeroBasedPageNumber + 1;
        int pageSize = page.getSize();
        boolean hasNext = page.hasNext();
        boolean hasPrevious = page.hasPrevious();

        return new PagingDto(oneBasedPageNumber, pageSize, hasNext, hasPrevious);
    }
}
