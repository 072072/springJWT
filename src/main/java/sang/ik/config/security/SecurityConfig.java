package sang.ik.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sang.ik.config.token.TokenHelper;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenHelper accessTokenHelper;
    private final CustomUserDetailsService userDetailService;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/exception/**",  "/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**"); // Spring Security를 무시할 URL 지정
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable() // http basic 인증 방법 비활성화
                .formLogin().disable() // form login 비활성화
                .csrf().disable()       // csrf 관련 설정 비활성화
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션관리 정책 설정 -> 세션을 유지하지 않도록 설정
                .and()
                .authorizeRequests()
                //.antMatchers("/**").permitAll();  // 일단 모든 url 접근 허용 -> 차후 개선될 예정
                .antMatchers(HttpMethod.GET, "/api/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/sign-in", "/api/sign-up","/api/refresh-token").permitAll()
                .antMatchers(HttpMethod.DELETE, "/api/members/{id}/**").access("@memberGuard.check(#id)")
                //access 기본문법 @<빈이름>.<메소드명>(<인자,#id로 하면 URL에 지정한 {id}가 매핑되어서 인자로 들어감>)
                .anyRequest().hasAnyRole("ADMIN")
                .and()
                .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())
                //인증된 사용자가 권한 부족 등의 사유로 인해 접근이 거부되었을 때 작동한 핸들러를 지정
                .and()
                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                //인증되지 않은 사용자의 접근이 거부되었을 때 작동할 핸들러를 지정
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(accessTokenHelper, userDetailService), UsernamePasswordAuthenticationFilter.class);
                //토큰으로 사용자를 인증하기 위해 직접 정의한 JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter의 이전 위치에 등록
                //JwtAuthenticationFilter는 필요한 의존성인 TokenService와 CustomUserDetailService를 주입받음.
    }

    /**
     * passwordEncoder의 구현체로 DelegatingPasswordEncoder를 사용해줍니다.
     * <p>
     * PasswordEncoderFactories 클래스에 팩토리 메소드를 이용하면 인스턴스를 생성할 수 있습니다.
     * <p>
     * 이것을 구현체로 선택한 이유는,
     * <p>
     * 비밀번호를 암호화하기 위한 다양한 알고리즘(bcrypt, md5, sha 계열 등)이 있는데,
     * <p>
     * 이 구현체를 이용하면 여러 알고리즘들을 선택적으로 편리하게 사용할 수 있습니다
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
