package com.handonbizmsg.mapper;

import com.handonbizmsg.domain.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;
import java.util.List;

@Mapper
public interface OrderMapper {
    void insertOrder(Order order);
    Optional<Order> findByOrderId(@Param("orderId") String orderId);
    List<Order> findAll();
}