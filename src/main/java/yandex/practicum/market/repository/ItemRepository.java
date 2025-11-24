package yandex.practicum.market.repository;


import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yandex.practicum.market.entity.ItemEntity;


@Repository
public interface ItemRepository extends R2dbcRepository<ItemEntity, Long> {

    @Query("""
            SELECT * FROM items
            WHERE (:search IS NULL OR title ILIKE '%' || :search || '%' OR description ILIKE '%' || :search || '%')
            ORDER BY :sortColumn
            LIMIT :pageSize 
            OFFSET :offset
            """)
    Flux<ItemEntity> findAllBySearchTerm(@Param("search") String search,
                                          @Param("sortColumn") String sortColumn,
                                          @Param("pageSize") Integer pageSize,
                                          @Param("offset") Integer offset);


    Flux<ItemEntity> findAllById(Iterable<Long> itemIds);

    Mono<Boolean> existsByTitle(String title);
}
