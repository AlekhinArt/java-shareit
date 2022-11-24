package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {

    List<Item> findAllByOwnerIdOrderById(long userId);

    @Query(" select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))" +
            " and i.available=TRUE")
    List<Item> search(String text);

    @Query(value = "select distinct i.item_id from bookings b right join items i on i.item_id = b.item_id " +
            "where owner_id = ? order by i.item_id", nativeQuery = true)
    List<Long> findIdByOwner(long id);
}
