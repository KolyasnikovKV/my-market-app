package yandex.practicum.market.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import yandex.practicum.market.dto.ItemDto;
import yandex.practicum.market.service.ItemOperationService;
import yandex.practicum.market.service.ItemService;
import yandex.practicum.market.service.SecurityService;
import yandex.practicum.market.types.SortType;
import yandex.practicum.market.dto.PagingDto;

import java.util.*;

@Controller
public class ItemController {

    private final ItemService itemService;
    private final ItemOperationService itemOperationService;
    private final SecurityService securityService;

    public ItemController(ItemService itemService, ItemOperationService itemOperationService, SecurityService securityService) {
        this.itemService = itemService;
        this.itemOperationService = itemOperationService;
        this.securityService = securityService;
    }



    @GetMapping("/items")
    public Mono<Rendering> showItems(
            @RequestParam(name = "search", defaultValue = "") String searchTerm,
            @RequestParam(name = "sort", defaultValue = "NO") SortType sortType,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            Model model,
            WebSession webSession
    ) {

        String sessionId = webSession.getId();
        return itemService.getItems(searchTerm, sortType, pageSize, pageNumber)
                .collectList()
                .zipWith(securityService.getCurrentUserId())
                .doOnNext((tuple) -> {
                    itemOperationService.getListOfListItemDto(tuple.getT2(), tuple.getT1());
                    model.addAttribute("items", tuple.getT1());
                    model.addAttribute("search", searchTerm);
                    model.addAttribute("sort", sortType);
                    model.addAttribute("paging",
                            new PagingDto(pageNumber,
                                    pageSize,
                                    (pageNumber - 1) * pageSize < tuple.getT1().size(),
                                    (pageNumber - 1) * pageSize > tuple.getT1().size()));
                })
                .thenReturn(Rendering.view("main")
                .build());
    }

    @GetMapping("/items/{id}")
    public Mono<String> showItem(
            @PathVariable Long id,
            Model model,
            WebSession session
    ) {
        return Mono.just(session.getId()) // Получаем ID сессии
                .flatMap(sessionId ->{
                        Mono<String> result = itemOperationService.getItem(id, sessionId) // Реактивный вызов сервиса
                                .doOnNext(itemDto -> model.addAttribute("item", itemDto)) // Добавляем в модель
                                .doOnNext(itemDto -> model.addAttribute("item", itemDto)) // Добавляем в модель
                                .thenReturn("item");
                        return result;
                } // Возвращаем имя шаблона
                )
                .onErrorResume(NoSuchElementException.class, ex -> {
                    // Обработка отсутствия элемента
                    model.addAttribute("error", "Товар не найден");
                    return Mono.just("error"); // Возвращаем страницу ошибки
                })
                .onErrorResume(Throwable.class, ex -> {
                    // Общая обработка ошибок
                    model.addAttribute("error", "Произошла ошибка при загрузке товара");
                    return Mono.just("error");
                });
    }
}
