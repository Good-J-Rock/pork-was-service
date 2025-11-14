package com.handonbizmsg.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.handonbizmsg.domain.Order;
import com.handonbizmsg.util.SignatureGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class OrderController {

    @Value("${naver.client-id}")
    private String clientId;
    @Value("${naver.client-secret}")
    private String clientSecret;
    @Value("${naver.api-url}")
    private String apiUrl;

    private final WebClient webClient;

    public OrderController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping(value = "/nToken")
    public void nToken() {
        // 1. 현재 timestamp (밀리초) 생성
        long timestamp = System.currentTimeMillis();

        // 2. 전자서명 생성
        String clientSecretSign = SignatureGenerator.generateClientSecretSign(
                clientId,
                clientSecret,
                timestamp
        );

        // 3. API 요청에 사용
        // 이 값들을 HTTP 헤더에 담아 API를 호출합니다.

        // 예: Header("X-API-KEY", clientId)
        // 예: Header("X-Timestamp", String.valueOf(timestamp))
        // 예: Header("X-Client-Secret-Sign", clientSecretSign)

        System.out.println("Timestamp: " + timestamp);
        System.out.println("Client Secret Sign: " + clientSecretSign);
//        return "Hello World";

//        return clientSecretSign;
    }

    @GetMapping(value = "/nOrderList")
//    public Map<String, Object> nOrderList() {
    public void nOrderList() {
        // 1. 현재 timestamp (밀리초) 생성
        long timestamp = System.currentTimeMillis();
        // 2. 전자서명 생성
        String clientSecretSign = SignatureGenerator.generateClientSecretSign(
                clientId,
                clientSecret,
                timestamp
        );
        log.info("Timestamp: " + timestamp);
        log.info("clientSecretSign: " + clientSecretSign);

        Mono<JsonNode> responseMono = webClient.get()
                .uri(apiUrl + "/v1/pay-order/seller/product-orders")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + clientSecretSign)
//                .header("X-Naver-Client-Secret", clientSecret)
                .retrieve()
                .bodyToMono(JsonNode.class);

        JsonNode body = responseMono.block(); // 동기 호출
        List<Order> orders = new ArrayList<>();
        log.info("body: " + body);
//        List<Order> orders = new ArrayList<>();

    }
}
