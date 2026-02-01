package com.ktran.flashsale_core.repositorys;

import com.ktran.flashsale_core.entities.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
}
