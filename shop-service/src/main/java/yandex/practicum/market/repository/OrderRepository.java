package yandex.practicum.market.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yandex.practicum.market.entity.OrderEntity;

@Repository
public interface OrderRepository extends R2dbcRepository<OrderEntity, Long> {

    Flux<OrderEntity> findAllByUserId(Long userId);

    Mono<OrderEntity> findByIdAndUserId(Long id, Long userId);
}
