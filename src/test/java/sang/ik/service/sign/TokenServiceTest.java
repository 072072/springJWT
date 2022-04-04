package sang.ik.service.sign;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import sang.ik.handler.JwtHandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    /**
     * TokenService는 다른 객체(JwtHandler)에 의존성을 가지고 있습니다.
     *
     * @InjectMocks를 필드에 선언하면, 의존성을 가지고 있는 객체들을 가짜로 만들어서 주입받을 수 있도록 합니다.
     * @Mock를 필드에 선언하면, 객체들을 가짜로 만들어서 @InjectMocks로 지정된 객체에 주입해줍니다.
     */
    @InjectMocks
    TokenService tokenService;
    @Mock
    JwtHandler jwtHandler;


    /**
     * TokenService는 @Value를 이용하여 설정 파일에서 값을 읽어와야합니다.
     * <p>
     * 하지만 우리는 TokenService에 대해서 단위 테스트만 수행할 것이기 때문에, 해당 값을 읽어올 수 없습니다.
     * <p>
     * 그렇다고 해서 값을 주입해주지 않는다면, (스트링 같은 경우) null 포인터를 가지기 때문에, 우리의 코드가 정상적으로 동작하는지 확인할 수 없습니다.
     * <p>
     * 이를 위해 취할 수 있는 첫번째 방법은 TokenService에 setter 메소드를 만들어주는 방법입니다.
     * <p>
     * 각각의 테스트를 수행하기 전에, 가짜로 생성된 TokenService 객체에 필요한 값들을 주입해주는 것입니다.
     * <p>
     * 하지만, 우리의 서비스에서는 TokenService가 굳이 setter 메소드를 가질 필요는 없습니다.
     * <p>
     * 테스트를 위해서 원래의 코드를 수정한다는 점은 썩 좋아보이지 않습니다.
     */
    @BeforeEach
    //각각의 테스트를 진행하기에 앞서 수행
    void beforeEach() {
        //setter 메소드를 사용하지 않고도 리플렉션을 이용해서 어떠한 객체의 필드 값을 임의의 값으로 주입해줄 수 있게 됨
        ReflectionTestUtils.setField(tokenService, "accessTokenMaxAgeSeconds", 10L);
        ReflectionTestUtils.setField(tokenService, "refreshTokenMaxAgeSeconds", 10L);
        ReflectionTestUtils.setField(tokenService, "accessKey", "accessKey");
        ReflectionTestUtils.setField(tokenService, "refreshKey", "refreshKey");
    }


    /**
     * Mockito에서 제공해주는
     * <p>
     * given() 메소드를 이용하면, 의존하는 가짜 객체의 행위가 반환해야할 데이터를 미리 준비하여 주입해줄 수도 있고,
     * <p>
     * verify() 메소드를 이용하면, 그 가짜 객체가 수행한 행위도 검증할 수 있습니다.
     */
    @Test
    void createAccessTokenTest() {
        // given
        given(jwtHandler.createToken(anyString(), anyString(), anyLong())).willReturn("access");

        // when
        String token = tokenService.createAccessToken("subject");

        // then
        assertThat(token).isEqualTo("access");
        verify(jwtHandler).createToken(anyString(), anyString(), anyLong());
    }

    @Test
    void createRefreshTokenTest() {
        // given
        given(jwtHandler.createToken(anyString(), anyString(), anyLong())).willReturn("refresh");

        // when
        String token = tokenService.createRefreshToken("subject");

        // then
        assertThat(token).isEqualTo("refresh");
        verify(jwtHandler).createToken(anyString(), anyString(), anyLong());
    }

    @Test
    void validateAccessTokenTest() {
        //given
        given(jwtHandler.validate(anyString(), anyString())).willReturn(true);

        //when,then
        assertThat(tokenService.validateAccessToken("token")).isTrue();
    }

    @Test
    void invalidateAccessTokenTest() {
        //given
        given(jwtHandler.validate(anyString(), anyString())).willReturn(false);

        //when, then
        assertThat(tokenService.validateRefreshToken("token")).isFalse();
    }

    @Test
    void validateRefreshTokenTest() {
        // given
        given(jwtHandler.validate(anyString(), anyString())).willReturn(true);

        // when, then
        assertThat(tokenService.validateRefreshToken("token")).isTrue();
    }

    @Test
    void invalidateRefreshTokenTest() {
        // given
        given(jwtHandler.validate(anyString(), anyString())).willReturn(false);

        // when, then
        assertThat(tokenService.validateRefreshToken("token")).isFalse();
    }

    @Test
    void extractAccessTokenSubjectTest(){
        //given
        String subject = "subject";
        given(jwtHandler.extractSubject(anyString(),anyString())).willReturn(subject);

        //when
        String result = tokenService.extractAccessTokenSubject(subject);

        //then
        assertThat(subject).isEqualTo(result);
    }

    @Test
    void extractRefreshTokenSubjectTest() {
        //given
        String subject = "subject";
        given(jwtHandler.extractSubject(anyString(), anyString())).willReturn(subject);

        //when
        String result = tokenService.extractRefreshTokenSubject(subject);

        //then
        assertThat(subject).isEqualTo(result);
    }
}
