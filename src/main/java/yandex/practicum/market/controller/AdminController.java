package yandex.practicum.market.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import yandex.practicum.market.service.AdminOperation;

import java.math.BigDecimal;

@Controller
public class AdminController {
    private final AdminOperation adminOperation;

    public AdminController(AdminOperation adminOperation){
        this.adminOperation = adminOperation;
    }

    @PostMapping("/admin/items/add")
    public Mono<ResponseEntity<String>> addItem(
            @RequestParam(name = "title") String title,
            @RequestParam(name = "description") String description,
            @RequestParam(name = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(name = "price") BigDecimal price
    ) {
        return adminOperation.addItem(title, description, imageFile, price)
                .map(message -> ResponseEntity.ok(message))
                .onErrorResume(ex -> {
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Произошла ошибка при добавлении товара"));
                });
    }
}
