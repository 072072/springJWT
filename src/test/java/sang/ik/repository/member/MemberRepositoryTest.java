package sang.ik.repository.member;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import sang.ik.entity.member.Member;
import sang.ik.entity.member.MemberRole;
import sang.ik.entity.member.Role;
import sang.ik.entity.member.RoleType;
import sang.ik.exception.MemberNotFoundException;
import sang.ik.repository.role.RoleRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static sang.ik.factory.entity.MemberFactory.createMember;
import static sang.ik.factory.entity.MemberFactory.createMemberWithRoles;

@DataJpaTest
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    RoleRepository roleRepository;
    @PersistenceContext
    EntityManager em;


    @Test
    void createAndReadTest() {
        //given
        Member member = createMember();

        //when
        memberRepository.save(member);
        clear();

        //then
        Member foundMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        assertThat(foundMember.getId()).isEqualTo(member.getId());
    }

    @Test
    void memberDateTest() {
        //given
        Member member = createMember();
        //when
        memberRepository.save(member);
        clear();
        //then
        Member foundMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        assertThat(foundMember.getCreatedAt()).isNotNull();
        assertThat(foundMember.getModifiedAt()).isNotNull();
        assertThat(foundMember.getCreatedAt()).isEqualTo(foundMember.getModifiedAt());
    }

    @Test
    void updateTest(){
        //given
        String updatedNickname= "updated";
        Member member = memberRepository.save(createMember());
        clear();

        //when
        Member foundMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        foundMember.updateNickname(updatedNickname);
        clear();

        //then
        Member updatedMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        assertThat(updatedMember.getNickname()).isEqualTo(updatedNickname);

    }

    @Test
    void deleteTest(){
        //given
        Member member = memberRepository.save(createMember());
        clear();

        //when
        memberRepository.delete(member);
        clear();

        //then
        //assertThatThrownBy(() -> {????????? ?????????}).isInstanceOf(??????????????????.class) ??? ?????? ????????? ??????
        assertThatThrownBy(()-> memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void findByEmailTest(){
        //given
        Member member = memberRepository.save(createMember());
        clear();

        //when
        Member foundMember = memberRepository.findByEmail(member.getEmail()).orElseThrow(MemberNotFoundException::new);

        //then
        assertThat(foundMember.getEmail()).isEqualTo(member.getEmail());
    }

    @Test
    void findByNicknameTest() {
        // given
        Member member = memberRepository.save(createMember());
        clear();

        // when
        Member foundMember = memberRepository.findByNickname(member.getNickname()).orElseThrow(MemberNotFoundException::new);

        // then
        assertThat(foundMember.getNickname()).isEqualTo(member.getNickname());
    }

    /**
     * email??? nickname??? unique ?????? ????????? ???????????? ?????????,
     * ????????? ???????????? ???????????? ??? DataIntegrityViolationException??? ??????
     */
    @Test
    void uniqueEmailTest() {
        // given
        Member member = memberRepository.save(createMember("email1", "password1", "username1", "nickname1"));
        clear();

        // when, then
        assertThatThrownBy(() -> memberRepository.save(createMember(member.getEmail(), "password2", "username2", "nickname2")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void uniqueNicknameTest() {
        // given
        Member member = memberRepository.save(createMember("email1", "password1", "username1", "nickname1"));
        clear();

        // when, then
        assertThatThrownBy(() -> memberRepository.save(createMember("email2", "password2", "username2", member.getNickname())))
                .isInstanceOf(DataIntegrityViolationException.class);
    }


    @Test
    void existsByEmailTest() {
        // given
        Member member = memberRepository.save(createMember());
        clear();

        // when, then
        assertThat(memberRepository.existsByEmail(member.getEmail())).isTrue();
        assertThat(memberRepository.existsByEmail(member.getEmail() + "test")).isFalse();
    }

    @Test
    void existsByNicknameTest() {
        // given
        Member member = memberRepository.save(createMember());
        clear();

        // when, then
        assertThat(memberRepository.existsByNickname(member.getNickname())).isTrue();
        assertThat(memberRepository.existsByNickname(member.getNickname() + "test")).isFalse();
    }

    /**
     * ??? ?????? ????????? ??? ?????????... cascade ?????? ??? ?????????...???
     * java9 ?????? List.of() ??????  --> ?????? ??????8?????? ????????? Arrays.asList()??? ??????
     */
    @Test
    void memberRoleCascadePersistTest() {
        // given
        List<RoleType> roleTypes = Arrays.asList(RoleType.ROLE_NORMAL, RoleType.ROLE_SPECIAL_BUYER, RoleType.ROLE_ADMIN);
        List<Role> roles = roleTypes.stream().map(roleType -> new Role(roleType)).collect(Collectors.toList());
        roleRepository.saveAll(roles); // RoleType DB??? ??????
        clear();

        //Member DB??? ?????? --> MemberRole ?????? ?????? ?????????.
        Member member = memberRepository.save(createMemberWithRoles(roleRepository.findAll()));
        clear();

        // when
        Member foundMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        Set<MemberRole> memberRoles = foundMember.getRoles();

        // then
        assertThat(memberRoles.size()).isEqualTo(roleTypes.size());
    }

    @Test
    void memberRoleCascadeDeleteTest() {
        // given
        List<RoleType> roleTypes = Arrays.asList(RoleType.ROLE_NORMAL, RoleType.ROLE_SPECIAL_BUYER, RoleType.ROLE_ADMIN);
        List<Role> roles = roleTypes.stream().map(roleType -> new Role(roleType)).collect(Collectors.toList());
        roleRepository.saveAll(roles);
        clear();

        Member member = memberRepository.save(createMemberWithRoles(roleRepository.findAll()));
        clear();

        // when
        memberRepository.deleteById(member.getId());
        clear();

        // then
        List<MemberRole> result = em.createQuery("select mr from MemberRole mr", MemberRole.class).getResultList();
        List<Member> memberList = memberRepository.findAll();
        assertThat(result.size()).isZero();
        assertThat(memberList.size()).isZero();
    }








    private void clear() {
        em.flush();
        em.clear();
    }

 /*   private Member createMemberWithRoles(List<Role> roles) {
        return new Member("email", "password", "username", "nickname", roles);
    }

    private Member createMember(String email, String password, String username, String nickname) {
        return new Member(email, password, username, nickname, emptyList());
    }

    private Member createMember() {
        return new Member("email", "password", "username", "nickname", emptyList());
    }*/
}
