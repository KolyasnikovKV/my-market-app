package ru.yandex.practicum.payment.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.payment.configuration.SecurityConfiguration;
import ru.yandex.practicum.payment.service.PaymentService;
import ru.yandex.practicum.payment.service.model.BalanceResponse;
import ru.yandex.practicum.payment.service.model.PaymentRequest;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@WebFluxTest(PaymentApi.class)
@Import(SecurityConfiguration.class)
class PaymentApiImplTest {

    @MockitoBean
    private PaymentService paymentService;

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ReactiveJwtDecoder reactiveJwtDecoder;

    @AfterEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }


    @Test
    @WithMockUser
    void getBalanceSuccessfulTest() {

        BalanceResponse expectedBalance = new BalanceResponse();
        expectedBalance.setBalance(BigDecimal.valueOf(3000));

        doReturn(new BalanceResponse().balance(BigDecimal.valueOf(3000)))
                .when(paymentService).getBalance();

        webTestClient
                .get()
                .uri("/balance")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody(BalanceResponse.class)
                .isEqualTo(expectedBalance);
    }

    @Test
    @WithMockUser
    void makePaymentSuccessfulTest() {

        PaymentRequest request = new PaymentRequest();
        request.setSum(BigDecimal.valueOf(1000));

        doReturn(Mono.just(-1))
                .when(paymentService).makePayment(any(Mono.class));

        webTestClient
                .post()
                .uri("/balance")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

    @Test
    @WithMockUser
    void makePaymentWithStatusConflictTest() {

        PaymentRequest request = new PaymentRequest();
        request.setSum(BigDecimal.valueOf(5000));

        doReturn(Mono.just(1))
                .when(paymentService).makePayment(any(Mono.class));

        webTestClient
                .post()
                .uri("/balance")
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(HttpStatusCode.valueOf(409))
                .expectBody().isEmpty();
    }
}