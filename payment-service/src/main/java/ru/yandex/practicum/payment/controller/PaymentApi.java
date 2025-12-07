package ru.yandex.practicum.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.payment.service.PaymentService;
import ru.yandex.practicum.payment.service.model.BalanceResponse;
import ru.yandex.practicum.payment.service.model.PaymentRequest;

@Controller
@RequiredArgsConstructor
public class PaymentApi implements ru.yandex.practicum.payment.service.api.PaymentApi {

    private final PaymentService paymentService;

    @Override
    public Mono<ResponseEntity<BalanceResponse>> getBalance(final ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(paymentService.getBalance()));
    }

    @Override
    public Mono<ResponseEntity<Void>> makePayment(
            Mono<PaymentRequest> paymentRequest,
            final ServerWebExchange exchange
    ) {
        return paymentService.makePayment(paymentRequest)
                .flatMap(comparison -> {
                    if (comparison <= 0) {
                        return Mono.just(ResponseEntity.ok().build());
                    } else {
                        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build());
                    }
                });
    }
}
