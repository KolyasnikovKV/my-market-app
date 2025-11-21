package yandex.practicum.market.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yandex.practicum.market.entity.OrderItemEntity;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
    @Query("SELECT SUM(od.price * od.quantity) FROM OrderItemEntity od WHERE od.order.id =:orderId")
    Optional<BigDecimal> sumTotalCostInOrder(@Param("orderId") Long orderId);
}
