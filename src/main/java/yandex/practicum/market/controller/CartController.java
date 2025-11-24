package yandex.practicum.market.controller;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.WebSession;

import reactor.core.publisher.Mono;
import yandex.practicum.market.service.CartOperationService;
import yandex.practicum.market.types.ActionType;
import yandex.practicum.market.service.CartService;

import java.util.*;

@Controller
public class CartController {

    private final CartService cartService;
    private final CartOperationService cartOperationService;

    public CartController(CartService cartService, CartOperationService cartOperationService) {
        this.cartService = cartService;
        this.cartOperationService = cartOperationService;
    }


    @GetMapping("/cart/items")
    public Mono<Rendering> showCart(Model model, WebSession session) {
        return cartService.getCart(session.getId())
                .collectList()
                .doOnNext(items -> {
                    java.math.BigDecimal total = items.stream()
                            .map(item -> item.getPrice().multiply(new java.math.BigDecimal(item.getCount())))
                            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                    model.addAttribute("items", items);
                    model.addAttribute("total", total);
                    model.addAttribute("empty", items.isEmpty());
                })
                .thenReturn(Rendering.view("cart")
                        .build());
    }

    @PostMapping("/cart/items")
    public Mono<Rendering> updateCartByCartPage(
            @PathVariable("id") Long itemId,
            @ModelAttribute ActionType action,
            WebSession session
    ) {

        return cartService.changeItemCountInCartByItemId(session.getId(), itemId, action)
                .thenReturn(Rendering.redirectTo("/cart/items")
                        .build());
    }

  }
