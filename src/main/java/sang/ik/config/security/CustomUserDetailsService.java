package sang.ik.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sang.ik.entity.member.Member;
import sang.ik.repository.member.MemberRepository;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;


    /**
     * @param userId  토큰에서 추출한 사용자의 id
     *
     *  orElse는 null이던말던 항상 불립니다.
     *  orElseGet은 null일 때만 불립니다.
     */
    @Override
    public CustomUserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        //Long.valueOf(String) --> return integer    :::   parseInt() --> return int
        Member member = memberRepository.findById(Long.valueOf(userId))
                .orElseGet(() -> new Member(null, null, null, null, Arrays.asList()));
        return new CustomUserDetails(
                String.valueOf(member.getId()),
                member.getRoles().stream()
                        .map(memberRole -> memberRole.getRole())
                        .map(role -> role.getRoleType())
                        .map(roleType -> roleType.toString())
                        // 등급은 String 타입으로 인식하기 때문에, Enum 타입인 RoleType을 String으로 변환
                        .map(SimpleGrantedAuthority::new).collect(Collectors.toSet())
        );
    }
}
