package com.handonbizmsg.service;

import com.handonbizmsg.domain.Order;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NaverApiService {

//    private final RestTemplate restTemplate;
    private final WebClient webClient;

    @Value("${naver.client-id}") private String clientId;
    @Value("${naver.client-secret}") private String clientSecret;
    @Value("${naver.api-url}") private String apiUrl;


    public List<Order> getNewOrders() {
        Mono<JsonNode> responseMono = webClient.get()
                .uri(apiUrl + "/v1/pay-order/seller/orders/:orderId/product-order-ids")
                .header("X-Naver-Client-Id", clientId)
                .header("X-Naver-Client-Secret", clientSecret)
                .retrieve()
                .bodyToMono(JsonNode.class);

        JsonNode body = responseMono.block(); // 동기 호출
        List<Order> orders = new ArrayList<>();
        if (body != null && body.has("data")) {
            for (JsonNode node : body.get("data")) {
                Order order = new Order();
                order.setOrderId(node.path("orderId").asText());
                order.setBuyerName(node.path("buyerName").asText());
                order.setBuyerPhone(node.path("buyerTel").asText());
                order.setProductName(node.path("productName").asText());
                order.setTotalAmount(node.path("totalPaymentAmount").asInt());
                order.setOrderDate(LocalDateTime.now());
                order.setStatus("NEW");
                orders.add(order);
            }
        }
        return orders;
    }
}
