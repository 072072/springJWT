package sang.ik.config.token;

import lombok.RequiredArgsConstructor;
import sang.ik.handler.JwtHandler;

@RequiredArgsConstructor
public class TokenHelper { // 기존의 TokenService 역할 수행

    private final JwtHandler jwtHandler;
    private final String key;
    private final long maxAgeSeconds;

    public String createToken(String subject){
        return jwtHandler.createToken(key, subject, maxAgeSeconds);
    }

    public boolean validate(String token) {
        return jwtHandler.validate(key, token);
    }

    public String extractSubject(String token) {
        return jwtHandler.extractSubject(key, token);
    }
}
