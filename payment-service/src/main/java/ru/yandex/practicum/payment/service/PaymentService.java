package ru.yandex.practicum.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.payment.service.model.BalanceResponse;
import ru.yandex.practicum.payment.service.model.PaymentRequest;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${service.default-balance}")
    private BigDecimal defaultBalance;

    public BalanceResponse getBalance() {
        return new BalanceResponse()
                .balance(defaultBalance);
    }
    public Mono<Integer> makePayment(Mono<PaymentRequest> paymentRequest) {
        return paymentRequest
                .map(request -> request.getSum().compareTo(defaultBalance));
    }
}