package com.ktran.flashsale_core.services.schedule;
import com.ktran.flashsale_core.entities.Order;
import com.ktran.flashsale_core.entities.OrderHistory;
import com.ktran.flashsale_core.entities.OrderItem;
import com.ktran.flashsale_core.repositorys.OrderHistoryRepository;
import com.ktran.flashsale_core.repositorys.OrderRepository;
import com.ktran.flashsale_core.repositorys.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderScannerService {

    private final OrderRepository orderRepository;
    private final OrderHistoryRepository historyRepository;
    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void scanExpiredOrders() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(10);
        List<Order> expiredOrders = orderRepository.findByStatusAndCreatedAtBefore("PENDING", cutoffTime);
        if (expiredOrders.isEmpty()) return;

        log.info("CronJob: Tìm thấy {} đơn treo quá hạn. Bắt đầu dọn dẹp...", expiredOrders.size());

        for (Order order : expiredOrders) {
            try {
                handleCancelExpiredOrder(order);
            } catch (Exception e) {
                log.error("CronJob Lỗi: Không thể hủy đơn {}: {}", order.getId(), e.getMessage());
            }
        }
    }
    private void handleCancelExpiredOrder(Order order) {
        order.setStatus("CANCELLED");
        orderRepository.save(order);

        OrderHistory history = OrderHistory.builder()
                .order(order)
                .status("CANCELLED")
                .note("Hệ thống tự động hủy do quá hạn thanh toán")
                .changedAt(LocalDateTime.now())
                .build();
        historyRepository.save(history);


        restoreStock(order);
        log.info("-> Đã hủy & hoàn kho đơn hàng ID: {}", order.getId());
    }

    private void restoreStock(Order order) {
        List<OrderItem> items = order.getOrderItems();
        if (items == null || items.isEmpty()) return;

        for (OrderItem item : items) {
            // 1. Hoàn kho Redis (Để người khác mua được ngay)
            String redisKey = "product:stock:" + item.getProduct().getId();
            try {
                redisTemplate.opsForValue().increment(redisKey, item.getQuantity());
            } catch (Exception e) {
                log.error("Lỗi hoàn kho Redis cho sản phẩm {}: {}", item.getProduct().getId(), e.getMessage());
            }


            try {
                productRepository.incrementStockAtomic(item.getProduct().getId(), item.getQuantity());
            } catch (Exception e) {
                log.error("Lỗi DB hoàn kho sp {}: {}", item.getProduct().getId(), e.getMessage());
            }
        }
    }
}
