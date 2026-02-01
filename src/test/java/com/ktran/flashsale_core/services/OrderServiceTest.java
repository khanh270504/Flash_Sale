package com.ktran.flashsale_core.services;


import com.ktran.flashsale_core.dtos.OrderDto;
import com.ktran.flashsale_core.dtos.OrderItemDto;
import com.ktran.flashsale_core.exceptions.AppException;
import com.ktran.flashsale_core.exceptions.ErrorCode;
import com.ktran.flashsale_core.services.rabbitmq.OrderProducer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderProducer orderProducer;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private DefaultRedisScript<Long> stockScript;

    @Test
    void createOrder_Success() {
        OrderDto request = new OrderDto();
        request.setUserId("1");
        request.setItems(List.of(new OrderItemDto(6L, 10)));

        when(redisTemplate.execute(eq(stockScript), anyList(), any())).thenReturn(1L);

        var response = orderService.createOrder(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("PROCESSING", response.getStatus());

        verify(orderProducer, times(1)).sendOrderToQueue(request);

    }
    @Test
    void createOrder_Fail_WhenOutOfStock() {

        OrderDto request = new OrderDto();
        request.setItems(List.of(new OrderItemDto(6L, 2)));

        when(redisTemplate.execute(eq(stockScript), anyList(), any())).thenReturn(0L);

        AppException exception = Assertions.assertThrows(AppException.class, () -> {
            orderService.createOrder(request);
        });

        Assertions.assertEquals(ErrorCode.PRODUCT_OUT_OF_STOCK, exception.getErrorCode());

        verify(orderProducer, never()).sendOrderToQueue(any());
    }
}
