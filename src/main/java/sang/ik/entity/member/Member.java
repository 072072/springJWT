package sang.ik.entity.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sang.ik.entity.common.EntityDate;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name="MEMBER_SEQ_GEN", //시퀀스 제너레이터 이름
        sequenceName="MEMBER_SEQ", //시퀀스 이름
        initialValue=1, //시작값
        allocationSize=1 //메모리를 통해 할당할 범위 사이즈
)
public class Member extends EntityDate {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE,  generator="MEMBER_SEQ_GEN")
    @Column(name="member_id")
    private Long id;

    @Column(nullable = false,length = 30,unique = true)
    private String email;

    private String password;

    @Column(nullable = false,length = 20)
    private String username;

    @Column(nullable = false,unique = true,length = 20)
    private String nickname;

    //cascade --> 연관된 엔티티도 영속상태로 만듦 , orphanRemoval --> 부모 엔티티와 연관관계가 끊어진 자식 엔티티를 자동으로 삭제 <고아객체제거>
    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MemberRole> roles;

    public Member(String email, String password, String username, String nickname, List<Role> roles){
        this.email = email;
        this.password = password;
        this.username = username;
        this.nickname = nickname;
        this.roles = roles.stream().map(r -> new MemberRole(this,r)).collect(toSet());
    }

    public void updateNickname(String nickname){
        this.nickname = nickname;
    }
}
