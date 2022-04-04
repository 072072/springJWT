package sang.ik.controller.sign;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sang.ik.dto.response.Response;
import sang.ik.dto.sign.SignInRequest;
import sang.ik.dto.sign.SignInResponse;
import sang.ik.dto.sign.SignUpRequest;
import sang.ik.service.sign.SignService;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

import static sang.ik.dto.response.Response.success;

@Api(value = "Sign Controller", tags = "Sign")
@RestController
@RequiredArgsConstructor
public class SignController {

    private final SignService signService;

    /**
     *  201 Created
     *  요청이 성공적이었으며 그 결과로 새로운 리소스가 생성되었습니다. 이 응답은 일반적으로 POST 요청 또는 일부 PUT 요청 이후에 따라옵니다.
     *
     *  회원가입에 성공하면 201 상태코드 응답
     *
     */
    @ApiOperation(value = "회원가입", notes = "회원가입을 한다.")
    @PostMapping("/api/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public Response signUp(@Valid @RequestBody SignUpRequest req){ // 전달받은 JSON을 @Vaild로 Response 객체 필드 값 검증
        signService.signUp(req);
        return success();
    }

    /**
     *
     *  성공하면 200코드, 데이터(여기에선 토큰)를 응답
     */
    @ApiOperation(value = "로그인", notes = "로그인을 한다.")
    @PostMapping("/api/sign-in")
    @ResponseStatus(HttpStatus.OK)
    public Response SignIn(@Valid @RequestBody SignInRequest req) {
        SignInResponse signInResponse = signService.signIn(req);
        return success(signInResponse);
    }


    /**
     * @RequestHeader는 required 옵션의 기본 설정 값이 true이기 때문에, 이 헤더 값이 전달되지 않았을 때 예외가 발생하게 됩니다.
     * 이 때 발생하는 예외가 MissingRequestHeaderException 입니다.
     */
    @ApiOperation(value = "토큰 재발급", notes = "리프레시 토큰으로 새로운 액세스 토큰을 발급 받는다.")
    @PostMapping("/api/refresh-token")
    @ResponseStatus(HttpStatus.OK)
    //요청에 포함되는 Authorization 헤더는 이미 전역적으로 지정되도록 설정해두었기 때문에, 해당 API에 필요한 요청 헤더는 @ApiIgnore를 선언해줍니다.
    public Response refreshToken(@ApiIgnore @RequestHeader(value ="Authorization")String refreshToken){
        return success(signService.refreshToken(refreshToken));
    }
}
