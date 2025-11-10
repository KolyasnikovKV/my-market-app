package yandex.practicum.market.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import yandex.practicum.market.service.ItemOperationService;
import yandex.practicum.market.service.ItemService;
import yandex.practicum.market.types.SortType;
import yandex.practicum.market.entity.ItemEntity;
import yandex.practicum.market.dto.ItemDto;
import yandex.practicum.market.dto.PagingDto;

import java.util.*;

@Controller
public class ItemController {

    private final ItemService itemService;
    private final ItemOperationService itemOperationService;

    public ItemController(ItemService itemService, ItemOperationService itemOperationService) {
        this.itemService = itemService;
        this.itemOperationService = itemOperationService;
    }

    @GetMapping("/items")
    public String showItems(
            @RequestParam(name = "search", defaultValue = "") String searchTerm,
            @RequestParam(name = "sort", defaultValue = "NO") SortType sortType,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            Model model,
            HttpSession session
    ) {
        Sort sort = SortType.toSort(sortType);
        String sessionId = session.getId();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        Page<ItemEntity> page = itemService.getItems(searchTerm, pageable);

        PagingDto pagingDto = PagingDto.of(page);
        model.addAttribute("paging", pagingDto);

        List<List<ItemDto>> listOfListItemDto = itemOperationService.getListOfListItemDto(sessionId, page);
        model.addAttribute("items", listOfListItemDto);
        model.addAttribute("search", searchTerm);
        model.addAttribute("sort", sortType);

        return "main";
    }

    @GetMapping("/items/{id}")
    public String showItem(
            @PathVariable Long id,
            Model model,
            HttpSession session
    ) throws NoSuchElementException {
        String sessionId = session.getId();
        ItemDto itemDto = itemOperationService.getItem(id, sessionId);
        model.addAttribute("item", itemDto);

        return "item";
    }
}
