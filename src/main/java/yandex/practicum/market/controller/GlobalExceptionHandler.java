package yandex.practicum.market.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Произошла ошибка: " + ex.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public Mono<ResponseEntity<String>> handleNoSuchElement(NoSuchElementException ex) {
        return Mono.just(ResponseEntity.notFound().build());
    }
}
