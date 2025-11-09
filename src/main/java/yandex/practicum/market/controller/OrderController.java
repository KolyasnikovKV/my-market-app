package yandex.practicum.market.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import yandex.practicum.market.entity.SessionEntity;
import yandex.practicum.market.entity.OrderEntity;
import yandex.practicum.market.dto.OrderDto;
import yandex.practicum.market.dto.factory.OrderDtoFactory;
import yandex.practicum.market.service.SessionService;
import yandex.practicum.market.service.OrderService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
public class OrderController {

    private final OrderService orderService;
    private final SessionService sessionService;
    private final OrderDtoFactory orderDtoFactory;

    public OrderController(OrderService orderService, SessionService sessionService, OrderDtoFactory orderDtoFactory) {
        this.orderService = orderService;
        this.sessionService = sessionService;
        this.orderDtoFactory = orderDtoFactory;
    }

    @PostMapping("/buy")
    public String buyItems(HttpSession session) {
        String sessionId = session.getId();
        SessionEntity cart = sessionService.getOrCreateSessionById(sessionId);

        OrderEntity order = orderService.buy(cart);
        sessionService.clear(cart);

        return "redirect:/orders/" + order.getId() + "?newOrder=true";
    }

    // Список заказов
    @GetMapping("/orders")
    public String showOrders(Model model, HttpSession session) {
        String sessionId = session.getId();
        SessionEntity sessionEntity = sessionService.getOrCreateSessionById(sessionId);

        List<OrderEntity> orders = orderService.getAllOrdersBySessionId(sessionEntity.getId());
        List<OrderDto> orderDTOs = new ArrayList<>(orders.size());

        for (OrderEntity order : orders) {
            BigDecimal totalCost = orderService.getOrderTotalCost(order.getId());
            OrderDto orderDto = orderDtoFactory.of(order, totalCost);
            orderDTOs.add(orderDto);
        }

        model.addAttribute("orders", orderDTOs);

        return "orders";
    }

    // Карточка заказа
    @GetMapping("/orders/{id}")
    public String showOrder(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean newOrder,
            Model model
    ) throws NoSuchElementException {
        OrderEntity order = orderService.getOrder(id);
        BigDecimal totalCost = orderService.getOrderTotalCost(order.getId());
        OrderDto orderDto = orderDtoFactory.of(order, totalCost);

        model.addAttribute("order", orderDto);
        model.addAttribute("newOrder", newOrder);

        return "order";
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElement(NoSuchElementException e) {
        return ResponseEntity.notFound().build();
    }
}
