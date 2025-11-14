package com.handonbizmsg.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Order {
    private Long id;
    private String orderId;
    private String buyerName;
    private String buyerPhone;
    private String productName;
    private Integer totalAmount;
    private LocalDateTime orderDate;
    private String status;
}
