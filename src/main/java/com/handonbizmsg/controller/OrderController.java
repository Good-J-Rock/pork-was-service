package com.handonbizmsg.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.handonbizmsg.domain.Order;
import com.handonbizmsg.service.AligoTalkService;
import com.handonbizmsg.util.SignatureGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class OrderController {
    @Autowired
    AligoTalkService aligoTalkService;

    @Value("${naver.client-id}")
    private String clientId;
    @Value("${naver.client-secret}")
    private String clientSecret;
    @Value("${naver.api-url}")
    private String apiUrl;

    // 🎯 API 요구 포맷: yyyy-MM-dd'T'HH:mm:ss.SSSXXX
    // XXX는 타임존 오프셋 (+09:00)을 의미하며, SSS는 밀리초 3자리를 보장합니다.
    private static final DateTimeFormatter API_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    private static final ZoneId KST_ZONE = ZoneId.of("Asia/Seoul");

//    private final WebClient webClient;
// WebClient를 빈(Bean)으로 등록하거나 인스턴스화 할 때 사용
    private final WebClient webClient = WebClient.builder()
        .baseUrl("https://api.commerce.naver.com/external") // Base URL 설정
        // 기타 필요한 설정을 추가합니다. (예: Headers, Connectors 등)
        .build();

//    https://api.commerce.naver.com/external/v1/oauth2/token?


//    public OrderController(WebClient webClient) {
//        this.webClient = webClient;
//    }

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

    /**
     * 특정 날짜와 시간(00시 00분 00초)을 네이버 API 요구 형식으로 포맷합니다.
     */
    public String getFromDateTime(int year, int month, int day) {
        // 1. 특정 일시 설정 (나노초까지 0으로 명시)
        LocalDateTime localDateTime = LocalDateTime.of(year, month, day, 0, 0, 0, 0);

        // 2. KST 타임존 적용
        ZonedDateTime targetDateTime = localDateTime.atZone(KST_ZONE);

        // 3. 포맷팅 (밀리초 3자리, 오프셋 포함)
        return targetDateTime.format(API_FORMATTER);
    }

    /**
     * 현재 시각을 네이버 API 요구 형식으로 포맷합니다.
     */
    public String getToDateTime() {
        // 현재 KST 시각을 가져옵니다.
        ZonedDateTime nowKst = ZonedDateTime.now(KST_ZONE);

        // 포맷팅 적용
        return nowKst.format(API_FORMATTER);
    }

    @GetMapping(value = "/nOrderList")
    public JsonNode nOrderList() throws UnsupportedEncodingException {
//    public void nOrderList() {
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

        // naver commerce oauth cert
        Mono<JsonNode> oauthRes = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/oauth2/token") // 전체 URL을 경로로 설정
                        .queryParam("client_id", clientId)
                        .queryParam("timestamp", timestamp) // Long 타입으로 처리
                        .queryParam("grant_type", "client_credentials")
                        .queryParam("client_secret_sign", clientSecretSign)
                        .queryParam("type", "SELF")
                        .build()
                )
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                // 3. 요청 전송 및 응답 수신
                .retrieve()

                // 4. 응답 코드가 4xx, 5xx일 경우 에러 처리
                .onStatus(status -> status.isError(), clientResponse -> {
                    // 에러 발생 시 응답 본문을 읽어 사용자 정의 예외로 변환
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(new RuntimeException(
                                    "Naver Token API Failed: " + clientResponse.statusCode() + " - " + errorBody)));
                })

                // 5. 응답 본문을 원하는 클래스 (NaverTokenResponse)의 Mono로 변환
                .bodyToMono(JsonNode.class);
        JsonNode body = oauthRes.block(); // 동기 호출
//        List<Order> orders = new ArrayList<>();
//        log.info("body: " + body);
        String accessToken = body.get("access_token").asText();
        String tokenType = body.get("token_type").asText();
        String expiresIn = body.get("expires_in").asText();
        log.info("accessToken: " + accessToken);
        log.info("tokenType: " + tokenType);
        log.info("expiresIn: " + expiresIn);
//        return body;


        // 조건형 상품 주문 상세 내역 조회
//        log.info("nowKst: " + nowKst);

        // 2. ISO 8601 포맷터 정의 (밀리초와 오프셋 포함)
        // 'T'는 리터럴 문자 T로 출력하기 위해 작은 따옴표로 감쌉니다.
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String fromDateTime = getFromDateTime(2025, 12, 5);
        String toDateTime = getToDateTime();

        Mono<JsonNode> responseMono = webClient.get()
//                .uri(apiUrl + "/v1/pay-order/seller/product-orders?from=2025-12-04T00:00:00")
//                .uri(uriBuilder -> uriBuilder
//                        // 1. 경로(Path) 설정
//                        .path("/v1/pay-order/seller/product-orders")
//                        .queryParam("from", encodedFrom)
////                        .queryParam("to", toDateTime)
////                        .queryParam("from", fromDateTime.toInstant().toEpochMilli())
////                        .queryParam("to", toDateTime.toInstant().toEpochMilli())
//
//                        // 2. 쿼리 파라미터(Query Parameter) 설정
//                        // ⚠️ "from" 값에 포함된 특수 문자(+ 등)는 WebClient가 자동으로 인코딩 처리합니다.
//
//                        // 3. URI 빌드 및 반환
//                        .build()
//                )
                .uri("/v1/pay-order/seller/product-orders?from={from}&to={to}", fromDateTime, toDateTime)
//                .uri("/v1/pay-order/seller/product-orders?from={from}", fromDateTime)
                // 최종적으로 만들어지는 URI 예시: /users?id=user123&limit=10
                .header("Accept", "application/json")
                // 1. 네이버 커머스 API에서 요구하는 인증 헤더 추가
//                .header("X-Naver-Client-Id", clientId)
//                .header("X-Naver-Client-Secret", clientSecret)
                .header("Authorization", "Bearer " + accessToken)
//                .header("X-Naver-Client-Secret", clientSecret)
                .retrieve()
//                .bodyToMono(JsonNode.class);
                // onStatus를 사용하면 4xx/5xx 응답 코드를 받았을 때 사용자 정의 예외 처리 가능
                .onStatus(status -> status.isError(), clientResponse -> {
                    // 💡 이 부분이 핵심! 응답 본문(errorBody)을 읽어 로그로 출력
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> {
                                log.error("Naver API Error Body (400): {}", errorBody);

                                // 에러 메시지에 응답 본문 내용을 포함하여 예외 발생
                                return Mono.error(new RuntimeException(
                                        "API Call Failed: " + clientResponse.statusCode() + " - Detail: " + errorBody));
                            });
                })
                .bodyToMono(JsonNode.class);



        JsonNode orderBody = responseMono.block(); // 동기 호출
//        List<Order> orders = new ArrayList<>();
        log.info("orderBody: " + orderBody);
        return orderBody;
//        List<Order> orders = new ArrayList<>();

    }

    @GetMapping(value = "/aligoTalkSend")
    public Mono<JsonNode> aligoTalkSend() {
        Map<String , Object> paramMap = new HashMap<>();
        paramMap.put("receiver1", "010-2250-6373");
        paramMap.put("message1", "Hello World");
        String receiver = "010-2250-6373";
        String message = "Hello World";
        return aligoTalkService.sendAligoTalk(receiver, message);
    }
}
