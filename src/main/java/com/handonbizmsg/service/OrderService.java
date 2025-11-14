package com.handonbizmsg.service;

import com.handonbizmsg.domain.Order;
import com.handonbizmsg.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final KakaoAlimtalkService kakaoService;

    public void processOrder(Order order) {
        if (orderMapper.findByOrderId(order.getOrderId()).isEmpty()) {
            orderMapper.insertOrder(order);
            kakaoService.sendAlimtalk(order);
        }
    }
}
