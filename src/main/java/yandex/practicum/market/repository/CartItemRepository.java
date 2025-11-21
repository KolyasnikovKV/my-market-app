package yandex.practicum.market.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import yandex.practicum.market.entity.CartItemEntity;
import yandex.practicum.market.entity.CartItemIdEntity;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, CartItemIdEntity> {
    @Transactional
    @Modifying
    @Query("DELETE FROM CartItemEntity cd WHERE cd.session.id = :sessionId")
    void deleteAllByCartId(Long sessionId);

    @Query("SELECT SUM(cd.price * cd.quantity) FROM CartItemEntity cd WHERE cd.session.id = :sessionId")
    Optional<BigDecimal> sumTotalCostInCartBySessionId(@Param("sessionId") Long sessionId);
}
