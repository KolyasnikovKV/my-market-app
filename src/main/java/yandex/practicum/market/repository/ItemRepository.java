package yandex.practicum.market.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import yandex.practicum.market.entity.ItemEntity;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
    @Query("""
            SELECT i FROM ItemEntity i WHERE
            (:searchTerm IS NULL OR
            LOWER(i.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
            LOWER(i.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
            """)
    Page<ItemEntity> findAllBySearchTerm(
            @Nullable @Param("searchTerm") String searchTerm,
            @NonNull Pageable pageable
    );

    boolean existsByTitle(String title);
}
