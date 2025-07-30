package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.RequestItem;

import java.util.List;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByUserId(Long userId);

    @Query("""
            select it
            from Item as it
            where available = true
            and (upper(it.name) like upper(concat('%',?1,'%'))
            or upper(it.description) like upper(concat('%',?1,'%')))
            """)
    List<Item> searchAvailableItems(String searchQuery);

    List<RequestItem> findAllByRequestId(long requestId);

    List<RequestItem> findAllByRequestIdIn(Set<Long> requestId);
}