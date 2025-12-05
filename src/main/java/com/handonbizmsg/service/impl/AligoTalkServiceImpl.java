package com.handonbizmsg.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.handonbizmsg.service.AligoTalkService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AligoTalkServiceImpl implements AligoTalkService {
    // 💡 application.yml 또는 application.properties에서 값을 주입받도록 설정
    @Value("${aligo.api-key}")
    private String apiKey;
    @Value("${aligo.user-id}")
    private String userId;
    @Value("${aligo.sender-key}")
    private String senderKey;
    @Value("${aligo.sender-number}")
    private String senderNumber;

    private final WebClient webClient;
    private static final String ALIMTALK_API_URL = "https://kakaoapi.aligo.in/akv10/alimtalk/send/";

    // WebClient는 생성자 주입을 통해 Bean으로 사용합니다.
    public AligoTalkServiceImpl(WebClient.Builder webClientBuilder) {
        // Base URL 없이 빌드하여 매 호출마다 전체 URL을 사용합니다.
        this.webClient = webClientBuilder.build();
    }

    /**
     * 알림톡 전송 요청을 처리합니다.
     * @param receiver1 첫 번째 수신자 번호
     * @param message1 첫 번째 메세지 내용
     * @return API 응답 JSON (Mono)
     */
    public Mono<JsonNode> sendAligoTalk(String receiver1, String message1) {

        // PHP 코드와 동일하게 현재 시간으로부터 10분 뒤를 예약 발송 시간으로 설정
        LocalDateTime sendTime = LocalDateTime.now().plusMinutes(10);
        String sendDate = sendTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        // 1. 전송할 파라미터를 Form Data 형태로 구성 (PHP의 http_build_query 역할)
        BodyInserters.FormInserter<String> formData = BodyInserters.fromFormData("apikey", apiKey)
                .with("userid", userId)
                .with("senderkey", senderKey)
                .with("tpl_code", "전송할 템플릿 코드") // ⚠️ 템플릿 코드 지정 필요
                .with("sender", senderNumber)
                .with("senddate", sendDate) // 예: 20251205102500

                // --- 첫 번째 알림톡 (receiver_1) ---
                .with("receiver_1", receiver1)
//                .with("recvname_1", "수신자 이름") // ⚠️ 사용자 명 지정 필요
//                .with("subject_1", "알림톡 제목") // ⚠️ 제목 지정 필요
                .with("recvname_1", "똥락") // ⚠️ 사용자 명 지정 필요
                .with("subject_1", "주문이 완료되었습니다") // ⚠️ 제목 지정 필요
                .with("message_1", message1);
                // 템플릿에 버튼이 있다면 JSON 문자열로 추가
                // .with("button_1", "{\"button\":[{\"name\":\"테스트 버튼\",\"linkType\":\"DS\"}]}")

                // --- 두 번째 알림톡 (receiver_2) 등을 필요에 따라 추가 ---
//                .with("receiver_2", "010-4598-1754")
//                .with("recvname_2", "똥순")
//                .with("subject_2", "두번째 알림톡 제목")
//                .with("message_2", "두번째 메세지 내용");

        return webClient.post()
                .uri(ALIMTALK_API_URL)
                // Content-Type을 application/x-www-form-urlencoded로 설정
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON) // 응답을 JSON으로 받도록 설정
                .body(formData) // Form Data 본문 전송
                .retrieve()

                // 4xx, 5xx 에러 처리 (API 응답 에러 핸들링)
                .onStatus(status -> status.isError(), clientResponse -> {
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(
                                    new RuntimeException("Alimtalk API Failed: " + clientResponse.statusCode() + " - " + errorBody)));
                })

                // 응답 본문을 JsonNode로 변환 (PHP의 json_decode 역할)
                .bodyToMono(JsonNode.class);
    }
}
