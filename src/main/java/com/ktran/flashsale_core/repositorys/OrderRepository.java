package com.ktran.flashsale_core.repositorys;

import com.ktran.flashsale_core.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(String userId);
    List<Order> findByStatusAndCreatedAtBefore(String status, LocalDateTime time);
}
