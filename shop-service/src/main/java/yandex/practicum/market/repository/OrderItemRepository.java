package yandex.practicum.market.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yandex.practicum.market.entity.ItemEntity;
import yandex.practicum.market.entity.OrderItemEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderItemRepository {

    private final DatabaseClient client;

    @Query("SELECT SUM(od.price * od.quantity) as sum FROM order_details od WHERE od.order_id =:orderId")
    public Mono<BigDecimal> sumTotalCostInOrder(@Param("orderId") Long orderId){
        return client.sql("""
                        SELECT SUM(od.price * od.quantity) as summa  FROM orders_items od WHERE od.order_id =:orderId"
                        """)
                .bind("orderId", orderId)
                .map((row, metadata) -> {
                    return row.get("summa", BigDecimal.class);
                })
                .first();
    }

    public Mono<Void> saveAll(List<OrderItemEntity> items) {
        return Flux.fromIterable(items)
                .flatMap(item ->
                        client.sql("INSERT INTO order_details(order_id, item_id, quantity) VALUES(:orderId, :itemId, :count)")
                                .bind("orderId", item.getOrderId())
                                .bind("itemId", item.getItemId())
                                .bind("count", item.getCount())
                                .fetch()
                                .rowsUpdated())
                .then();
    }

    public Flux<OrderItemEntity> findByOrderId(Long orderId) {
        return client.sql("""
                        SELECT oi.item_id, items.title, items.description, items.img_path, items.price, oi.count
                        FROM order_details AS oi 
                        JOIN items ON oi.item_id = items.id
                        WHERE oi.order_id = :orderId
                        """)
                .bind("orderId", orderId)
                .map((row, metadata) -> {
                    Long itemId = row.get("item_id", Long.class);
                    String title = row.get("title", String.class);
                    String description = row.get("description", String.class);
                    Integer count = row.get("count", Integer.class);
                    BigDecimal price = row.get("price", BigDecimal.class);

                    return OrderItemEntity.builder()
                            .id(itemId)
                            .title(title)
                            .description(description)
                            .price(price)
                            .count(count)
                            .build();
                })
                .all();
    }

    public Flux<OrderItemEntity> findAll() {
        return client.sql("SELECT order_id, item_id, title, description, quantity, price FROM order_details")
                .map((row, metadata) -> {
                    Long orderId = row.get("order_id", Long.class);
                    Long itemId = row.get("item_id", Long.class);
                    String title = row.get("title", String.class);
                    String description = row.get("description", String.class);
                    Integer quantity = row.get("quantity", Integer.class);
                    BigDecimal price = row.get("price", BigDecimal.class);

                    return new OrderItemEntity(
                            null,
                            orderId,
                            itemId,
                            title,
                            description,
                            quantity,
                            price
                    );
                })
                .all();
    }
}
