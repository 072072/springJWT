package sang.ik.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import sang.ik.entity.member.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {


    Optional<Member> findByEmail(String email);
    Optional<Member> findByNickname(String nickname);

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}
