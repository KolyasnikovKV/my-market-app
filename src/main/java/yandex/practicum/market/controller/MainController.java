package yandex.practicum.market.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class MainController {

    // Редирект на главную страницу товаров
    @GetMapping("/")
    public String redirectToMain() {
        return "redirect:/items";
    }


}
