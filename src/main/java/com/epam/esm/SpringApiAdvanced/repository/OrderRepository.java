package com.epam.esm.SpringApiAdvanced.repository;

import com.epam.esm.SpringApiAdvanced.repository.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface OrderRepository extends CrudDao<Order, Integer> {

    Page<Order> findByUserId(Integer id, Pageable pageable);

    Order findLastAddedOrder();
}
