package yandex.practicum.market.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Controller
public class MainController {

    @GetMapping("/")
    public Mono<String> redirectToMain() {
        return Mono.just("redirect:/items");
    }
}
