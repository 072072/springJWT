package sang.ik.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import sang.ik.config.token.TokenHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

//직접 @Component를 선언하면 자동으로 필터 체인에 등록되기 때문에, 중복 등록을 방지하기 위해 @Component는 생략
//이미 순서 제어를 위해, SecurityConfig에서 직접 생성하여 필터 체인에 등록해주었음
//필요한 의존성들은 SecurityConfig에서 받아다가 주입

// SecurityContextHolder에 있는 ContextHolder에다가 Authentication 인터페이스의 구현체 CustomAuthenticationToken를 등록해주는 작업

//CustomAuthenticationToken은 CustomUserDetailsService를 이용하여 조회된 사용자의 정보 CustomUserDetails와 요청 토큰의 타입을 저장해줍니다.
//액세스 토큰과 리프레시 토큰을 구분 짓기 위해 별도의 검증 작업을 수행
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final TokenHelper accessTokenHelper;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = extractToken(request);
        if(validateToken(token)){
            setAuthentication("access", token);
        }
        chain.doFilter(request,response);
    }


    private String extractToken(ServletRequest request){
        return ((HttpServletRequest) request).getHeader("Authorization");
    }

    private boolean validateToken(String token){
        return token != null && accessTokenHelper.validate(token);
    }

    private void setAuthentication(String type, String token){
        String userId = accessTokenHelper.extractSubject(token);
        CustomUserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        SecurityContextHolder.getContext().setAuthentication(new CustomAuthenticationToken(type, userDetails, userDetails.getAuthorities()));
    }
}
