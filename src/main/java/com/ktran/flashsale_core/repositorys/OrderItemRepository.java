package com.ktran.flashsale_core.repositorys;

import com.ktran.flashsale_core.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
