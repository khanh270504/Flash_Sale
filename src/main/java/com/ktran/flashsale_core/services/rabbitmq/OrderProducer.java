package com.ktran.flashsale_core.services.rabbitmq;


import com.ktran.flashsale_core.configuration.RabbitConfig;
import com.ktran.flashsale_core.dtos.OrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderProducer {
    private final RabbitTemplate rabbitTemplate;

    public void sendOrderToQueue(OrderDto dto) {
        log.info("Bắn đơn hàng của user {} vào RabbitMQ...", dto.getUserId());
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTING_KEY,
                dto
        );
    }
}
