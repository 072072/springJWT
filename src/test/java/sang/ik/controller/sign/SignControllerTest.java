package sang.ik.controller.sign;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import sang.ik.dto.sign.RefreshTokenResponse;
import sang.ik.dto.sign.SignInRequest;
import sang.ik.dto.sign.SignUpRequest;
import sang.ik.factory.dto.SignUpRequestFactory;
import sang.ik.service.sign.SignService;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sang.ik.factory.dto.SignInRequestFactory.createSignInRequest;
import static sang.ik.factory.dto.SignInResponseFactory.createSignInResponse;

@ExtendWith(MockitoExtension.class)
public class SignControllerTest {

    @InjectMocks
    SignController signController;
    @Mock
    SignService signService;
    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper(); // 객체를 json 문자열로 변환

    @BeforeEach
    void beforeEach(){
        mockMvc = MockMvcBuilders.standaloneSetup(signController).build();
    }

    @Test
    void signUpTest()throws Exception{
        //given
        SignUpRequest req = SignUpRequestFactory.createSignUpRequest();

        //when, then
        mockMvc.perform(post("/api/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))   // -->  객체를 json 문자열로 변환 가능. content에 넣어주면 요청 바디에 담긴다.
                .andExpect(status().isCreated());

        verify(signService).signUp(req);
    }


    @Test
    void signInTest()throws Exception{
        //given
        SignInRequest req = createSignInRequest("email@email.com", "123456a!");
        given(signService.signIn(req)).willReturn(createSignInResponse("access", "refresh"));

        //when, then
        mockMvc.perform(
                        post("/api/sign-in")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data.accessToken").value("access"))  // given에서 준 값이 응답JSON에 포함되어 있는지 검증
                .andExpect(jsonPath("$.result.data.refreshToken").value("refresh"));

        verify(signService).signIn(req);
    }

    @Test //"/api/sign-up"의 응답 결과로 반환되는 JSON 문자열 또한 올바르게 제거되는지 다시 한번 검증
    void ignoreNullValueInJsonResponseTest()throws Exception{
        //given
        SignUpRequest req = SignUpRequestFactory.createSignUpRequest();

        //when, then
        mockMvc.perform(
                        post("/api/sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result").doesNotExist());
    }

    @Test
    void refreshTokenTest()throws Exception{
        //given
        given(signService.refreshToken("refreshToken")).willReturn(new RefreshTokenResponse("accessToken"));

        //when, then
        mockMvc.perform(
                        post("/api/refresh-token")
                                .header("Authorization", "refreshToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data.accessToken").value("accessToken"));
    }


}
