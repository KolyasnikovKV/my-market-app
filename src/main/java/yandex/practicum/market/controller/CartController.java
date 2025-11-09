package yandex.practicum.market.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import yandex.practicum.market.entity.SessionEntity;
import yandex.practicum.market.dto.ItemDto;
import yandex.practicum.market.service.CartOperationService;
import yandex.practicum.market.types.ActionType;
import yandex.practicum.market.service.SessionService;

import java.math.BigDecimal;
import java.util.*;

import yandex.practicum.market.types.SortType;

@Controller
public class CartController {

    private final SessionService sessionService;
    private final CartOperationService cartOperationService;

    public CartController(SessionService sessionService, CartOperationService cartOperationService) {
        this.sessionService = sessionService;
        this.cartOperationService = cartOperationService;
    }

    @GetMapping("/cart/items")
    public String showCart(Model model, HttpSession session) {
        String sessionId = session.getId();
        SessionEntity sessionEntity = sessionService.getOrCreateSessionById(sessionId);
        List<ItemDto> items = cartOperationService.getItemDtos(sessionEntity);
        BigDecimal totalCost = sessionService.getCartTotalCostBySessionId(sessionEntity.getId());

        model.addAttribute("items", items);
        model.addAttribute("total", totalCost);
        model.addAttribute("empty", items.isEmpty());

        return "cart";
    }

    @PostMapping("/items")
    public String updateCartByMainPage(
            @RequestParam @NonNull Long id,
            @RequestParam(name = "search", defaultValue = "") String searchTerm,
            @RequestParam(name = "sort", defaultValue = "NO") SortType sortType,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam ActionType action,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) throws NoSuchElementException {
        String sessionId = session.getId();
        cartOperationService.updateCart(sessionId, id, action);
        redirectAttributes.addAttribute("search", searchTerm);
        redirectAttributes.addAttribute("sort", sortType);
        redirectAttributes.addAttribute("pageSize", pageSize);
        redirectAttributes.addAttribute("pageNumber", pageNumber);
        return "redirect:/items";
    }

    @PostMapping("/cart/items")
    public String updateCartByCartPage(
            @RequestParam @NonNull Long id,
            @RequestParam ActionType action,
            HttpSession session
    ) throws NoSuchElementException {
        String sessionId = session.getId();
        cartOperationService.updateCart(sessionId, id, action);

        return "redirect:/cart/items";
    }

    @PostMapping("/items/{id}")
    public String updateCartByItemPage (
            @PathVariable Long id,
            @RequestParam ActionType action,
            HttpSession session
    ) throws NoSuchElementException {
        String sessionId = session.getId();
        cartOperationService.updateCart(sessionId, id, action);

        return "redirect:/items/" + id;
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElement(NoSuchElementException e) {
        return ResponseEntity.notFound().build();
    }
}
