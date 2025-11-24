package yandex.practicum.market.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import yandex.practicum.market.service.CartService;
import yandex.practicum.market.service.OrderService;

@Controller
public class OrderController {

    private static final String PARAM_NEW_ORDER_IS_TRUE = "?newOrder=true";

    private final OrderService orderService;
    private final CartService cartService;

    public OrderController(OrderService orderService, CartService cartService) {
        this.orderService = orderService;
        this.cartService = cartService;

    }

    /**
     * Покупка товаров из корзины (выполняет покупку товаров в корзине и очищает ее)
     *
     * @return Редирект на "/orders/{id}?newOrder=true"
     */
    @PostMapping("/buy")
    public Mono<Rendering> buyFromCart(@SessionAttribute WebSession webSession) {

        return orderService.createOrder(webSession.getId())
                .map(orderId -> Rendering.redirectTo("/orders/" + orderId + PARAM_NEW_ORDER_IS_TRUE)
                        .build());
    }

    /**
     * Список заказов
     *
     * @param model Модель
     * @return Шаблон "orders.html"
     */
    @GetMapping("/orders")
    public Mono<Rendering> getOrders(Model model, @SessionAttribute WebSession webSession) {

        return orderService.findOrders(webSession.getId())
                .collectList()
                .doOnNext(orders -> model.addAttribute("orders", orders))
                .thenReturn(Rendering.view("orders")
                        .build());
    }

    /**
     * Карточка заказа
     *
     * @param orderId  Идентификатор заказа
     * @param newOrder true, если переход со страницы оформления заказа (по умолчанию, false)
     * @param model    Модель
     * @return Шаблон "order.html"
     */
    @GetMapping("/orders/{id}")
    public Mono<Rendering> getOrderById(
            @PathVariable("id") Long orderId,
            @RequestParam(required = false, defaultValue = "false") Boolean newOrder,
            Model model,
            @SessionAttribute WebSession webSession
    ) {

        return orderService.findOrderById(orderId, webSession.getId())
                .doOnNext(order -> {
                    model.addAttribute("order", order);
                    model.addAttribute("newOrder", newOrder);
                })
                .thenReturn(Rendering.view("orders")
                        .build());
    }

}
