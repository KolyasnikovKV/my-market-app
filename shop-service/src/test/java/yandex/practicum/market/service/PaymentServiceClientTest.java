package yandex.practicum.market.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceClientTest {
/*
    @Mock
    PaymentApi paymentApi;

    @Captor
    ArgumentCaptor<PayRequest> payRequestCaptor;

    PaymentServiceClient client;

    private static final String USER = "alice";

    @BeforeEach
    void setUp() {
        client = new PaymentServiceClient(paymentApi);
        // проставляем валюту, т.к. поле приватное и берётся из @Value
        ReflectionTestUtils.setField(client, "currency", "EUR");
    }

    @Test
    void getBalance_shouldPassUser_andReturnBalanceResponse() {
        // given
        var balance = new BalanceResponse().amount(123.45);
        when(paymentApi.getBalance(eq(USER))).thenReturn(Mono.just(balance));

        Authentication auth = new UsernamePasswordAuthenticationToken(USER, "n/a");

        // when & then
        StepVerifier.create(
                        client.getBalance()
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
                )
                .expectNext(balance)
                .verifyComplete();

        verify(paymentApi).getBalance(USER);
    }

    @Test
    void pay_whenSuccess_shouldReturnPayResponse_andSendCorrectBody_withUser() {
        // given
        var amount = new BigDecimal("99.99");
        var apiResponse = new PayResponse().success(true);
        when(paymentApi.pay(eq(USER), any(PayRequest.class))).thenReturn(Mono.just(apiResponse));

        Authentication auth = new UsernamePasswordAuthenticationToken(USER, "n/a");

        // when & then
        StepVerifier.create(
                        client.pay(amount)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
                )
                .expectNext(apiResponse)
                .verifyComplete();

        verify(paymentApi).pay(eq(USER), payRequestCaptor.capture());
        PayRequest sent = payRequestCaptor.getValue();
        assertThat(sent.getAmount()).isEqualTo(99.99);
        assertThat(sent.getCurrency()).isEqualTo("EUR");
    }

    @Test
    void pay_whenApiErrors_shouldPropagateError() {
        // given
        var amount = new BigDecimal("10.00");
        var boom = new RuntimeException("Boom");
        when(paymentApi.pay(eq(USER), any(PayRequest.class))).thenReturn(Mono.error(boom));

        Authentication auth = new UsernamePasswordAuthenticationToken(USER, "n/a");

        // when & then
        StepVerifier.create(
                        client.pay(amount)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
                )
                .expectErrorMatches(err -> err == boom)
                .verify();
    }*/
}
