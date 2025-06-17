package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long userId);

    @Query("""
                SELECT i FROM Item i
                WHERE i.available = true
                AND (LOWER(i.name) LIKE %:text% OR LOWER(i.description) LIKE %:text%)
            """)
    List<Item> search(@Param("text") String text);

    List<Item> findAllByRequestIdIn(List<Long> requestIds);

    List<Item> findAllByRequestId(Long requestId);
}