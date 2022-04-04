package sang.ik.controller.exception;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sang.ik.exception.AccessDeniedException;
import sang.ik.exception.AuthenticationEntryPointException;
import springfox.documentation.annotations.ApiIgnore;

// 시큐리티에서 발생한 예외를 리다이렉트 해주는 것이라서 스웨거에 필요없음.
@ApiIgnore
@RestController
public class ExceptionController {

    @GetMapping("/exception/entry-point")
    public void entryPoint(){
        throw new AuthenticationEntryPointException();
    }

    @GetMapping("/exception/access-denied")
    public void accessDenied(){
        throw new AccessDeniedException();
    }
}
