package com.handonbizmsg.util;

import org.mindrot.jbcrypt.BCrypt;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SignatureGenerator {

    /**
     * client_secret_sign을 생성합니다.
     * @param clientId API 클라이언트 ID
     * @param clientSecret API 클라이언트 Secret (Salt 역할)
     * @param timestamp 현재 시간의 Unix Time (밀리초)
     * @return Base64 인코딩된 client_secret_sign
     */
    public static String generateClientSecretSign(String clientId, String clientSecret, long timestamp) {
        // 1. Password 문자열 생성 (clientId_timestamp)
        String password = clientId + "_" + timestamp;

        // 2. bcrypt 해싱 수행
        // clientSecret을 Salt로 사용하여 password를 해싱합니다.
        // BCrypt.hashpw(password, salt)
        String hashed = BCrypt.hashpw(password, clientSecret);

        // 3. Base64 인코딩
        // API 서비스에 따라 URL-safe 인코딩이 필요할 수 있습니다.
        // 여기서는 기본 Base64 인코딩을 사용합니다.
        String clientSecretSign = Base64.getEncoder()
                .encodeToString(hashed.getBytes(StandardCharsets.UTF_8));

        return clientSecretSign;
    }
}
