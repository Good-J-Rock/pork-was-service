package com.handonbizmsg.service;

import com.handonbizmsg.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoAlimtalkService {

//    private final RestTemplate restTemplate;
    private final WebClient webClient;

    @Value("${kakao.api-url}") private String apiUrl;
    @Value("${kakao.access-token}") private String accessToken;
    @Value("${kakao.template-code}") private String templateCode;

    public void sendAlimtalk(Order order) {
        webClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "template_code", templateCode,
                        "receiver_uuids", new String[]{order.getBuyerPhone()}, // 실사용 시 UUID 필요
                        "template_args", Map.of(
                                "name", order.getBuyerName(),
                                "product", order.getProductName(),
                                "amount", order.getTotalAmount()
                        )
                ))
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(Throwable::printStackTrace)
                .subscribe(); // 비동기 전송
    }
}
