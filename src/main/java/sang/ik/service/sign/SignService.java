package sang.ik.service.sign;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sang.ik.config.token.TokenHelper;
import sang.ik.dto.sign.RefreshTokenResponse;
import sang.ik.dto.sign.SignInRequest;
import sang.ik.dto.sign.SignInResponse;
import sang.ik.dto.sign.SignUpRequest;
import sang.ik.entity.member.Member;
import sang.ik.entity.member.RoleType;
import sang.ik.exception.*;
import sang.ik.repository.member.MemberRepository;
import sang.ik.repository.role.RoleRepository;


/**
 * SignService.refreshToken 메소드는 별도의 데이터베이스 작업이 없기 때문에, 트랜잭션을 열어줄 필요가 없습니다.
 * 이로 인해 클래스 레벨에 @Transactional을 선언하면,
 * 데이터베이스 작업이 없더라도 SignService.refreshToken 메소드에서도 불필요하게 트랜잭션을 열어야합니다.
 */
//@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class SignService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenHelper accessTokenHelper; // 1
    private final TokenHelper refreshTokenHelper; // 1


    @Transactional
    public void signUp(SignUpRequest req) {
        validateSignUpInfo(req);   // 중복성 검사

        Member member = SignUpRequest.toEntity(req,
                roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new),
                passwordEncoder);

        memberRepository.save(member);  // Dto --> Entity 변환
    }

    @Transactional(readOnly = true)
    public SignInResponse signIn(SignInRequest req) {
        Member member = memberRepository.findByEmail(req.getEmail()).orElseThrow(LoginFailureException::new);

        validatePassword(req, member);

        String subject = createSubject(member);
        String accessToken = accessTokenHelper.createToken(subject);
        String refreshToken = refreshTokenHelper.createToken(subject);

        return new SignInResponse(accessToken, refreshToken);
    }


    public RefreshTokenResponse refreshToken(String rToken) {

        validateRefreshToken(rToken);

        String subject = refreshTokenHelper.extractSubject(rToken);
        String accessToken = accessTokenHelper.createToken(subject);

        return new RefreshTokenResponse(accessToken);
    }

    private void validatePassword(SignInRequest req, Member member) {
        if (!passwordEncoder.matches(req.getPassword(), member.getPassword())) {
            throw new LoginFailureException();
        }
    }


    private void validateRefreshToken(String rToken) {
        if (!refreshTokenHelper.validate(rToken)) {
            throw new AuthenticationEntryPointException();
        }
    }

    private void validateSignUpInfo(SignUpRequest req) {
        if (memberRepository.existsByEmail(req.getEmail())) {
            throw new MemberEmailAlreadyExistsException(req.getEmail());
        }
        if (memberRepository.existsByNickname(req.getNickname())) {
            throw new MemberNicknameAlreadyExistsException(req.getNickname());
        }
    }

    private String createSubject(Member member) {
        return String.valueOf(member.getId());
    }
}
