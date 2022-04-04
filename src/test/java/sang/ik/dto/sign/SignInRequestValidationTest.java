package sang.ik.dto.sign;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static sang.ik.factory.dto.SignInRequestFactory.*;

public class SignInRequestValidationTest {

    // 검증 작업을 수행하기 위해 Validator를 빌드
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validateTest() {
        //given
        SignInRequest req = createSignInRequest();

        //when  --> 검증 수행. 제약 조건을 위반한 내용들을 응답 결과로 받음
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(req);

        //then  --> 제약 조건을 모두 지키고 올바르게 검증 되었다면, 응답 결과는 비어있음.
        assertThat(validate).isEmpty();
    }

    @Test
    void invalidateByNotFormattedEmailTest() {
        //given
        String invalidValue = "email";
        SignInRequest req = createSignInRequestWithEmail(invalidValue);

        //when
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(req);

        //then --> 제약조건을 위반하면 응답결과는 비어있지 않음.
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet()).contains(invalidValue));
        // --> 응답결과에서 제약 조건을 위반한 객체를 꺼내서 , <given> 에서 설정해두었던 위반된 값을 가지고 있는지 확인.
    }

    @Test
    void invalidateByEmptyEmailTest() {
        // given
        String invalidValue = null;
        SignInRequest req = createSignInRequestWithEmail(invalidValue);

        // when
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByBlankEmailTest() {
        // given
        String invalidValue = " ";
        SignInRequest req = createSignInRequestWithEmail(invalidValue);

        // when
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }


    @Test
    void invalidateByEmptyPasswordTest() {
        // given
        String invalidValue = null;
        SignInRequest req = createSignInRequestWithPassword(invalidValue);

        // when
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByBlankPasswordTest() {
        // given
        String invalidValue = " ";
        SignInRequest req = createSignInRequestWithPassword(" ");

        // when
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

/*    private SignInRequest createRequest() { // 6
        return new SignInRequest("email@email.com", "123456a!");
    }

    private SignInRequest createRequestWithEmail(String email) { // 7
        return new SignInRequest(email, "123456a!");
    }

    private SignInRequest createRequestWithPassword(String password) { // 8
        return new SignInRequest("email@email.com", password);
    }*/
}
