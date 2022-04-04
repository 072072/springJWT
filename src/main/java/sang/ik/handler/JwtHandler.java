package sang.ik.handler;


import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtHandler {

    private String type = "Bearer"; // 생성해낸 토큰이 어떤 타입인지(여기서는 jwt)

    /**
     *  Base64로 인코딩된 key 값을 받고,
     *  토큰에 저장될 데이터 subject,
     *  만료 기간 maxAgeSeconds를 초단위로 받아서 토큰을 만들어주는 작업을 수행
     */
    public String createToken(String encodedKey, String subject,long maxAgeSeconds){
        Date now = new Date();
        return type + Jwts.builder()    // jwt 빌드 시작
                .setSubject(subject)    // 토근에 저장될 데이터 지정
                .setIssuedAt(now)       // 토근 발급일 지정  --> Date는 ms 단위로 입력받기 때문에 *1000 해준다
                .setExpiration(new Date(now.getTime() + maxAgeSeconds*1000L))   // 토큰 만료 일자를 지정
                .signWith(SignatureAlgorithm.HS256, encodedKey)      // 파라미터로 받은 key로 SHA-256 알고리즘을 사용하여 서명
                .compact();                                                     // 토큰생성
   }

    /**
     *  토큰에서 subject를 추출해냅니다. 토큰을 파싱하고, 바디에서 subject를 꺼내올 수 있습니다.
     *  우리의 서비스에서는, 토큰의 subject로 Member의 id가 저장되기 때문에, 이를 이용하여 사용자를 인증할 수 있을 것이라 예상
     */
    public String extractSubject(String encodedKey, String token){
        return parse(encodedKey,token).getBody().getSubject();
    }


    /**
     *
     *  유효성 검증 --> 토큰을 파싱하면서 jwt 관련 예외가 발생했다면, 유효하지않은 토큰으로 판단
     */
    public boolean validate(String encodedKey, String token){
        try {
            parse(encodedKey,token);
            return true;
        }catch (JwtException e){
            return false;
        }
    }

    private Jws<Claims> parse(String key, String token){
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(untype(token));
    }

    private String untype(String token){
        return token.substring(type.length());
    }
}
