package sang.ik.entity.member;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * - Composite Key를 만들 때 주의할 점
 *
 * composite key를 만들 때는, 기본적으로 알파벳 순으로 key가 만들어지게 됩니다.
 *
 * 이 때문에, 위 예시에서는 member, role의 순서로 key가 만들어집니다.
 *
 * composite key에서는 key들의 순서가 중요합니다.
 *
 * 인덱스 구조가 첫번째 필드로 정렬된 뒤에, 그 다음으로 두번째 필드로 정렬되기 때문에,
 *
 * 만약 중복도가 높은 필드가 첫번째로 생성된다면, 필터링되는 레코드가 적어서 인덱스의 효과를 보지 못하게 됩니다.
 *
 * 우리가 진행하고 있는 프로젝트에서는, Role은 몇개밖에 생성되지 않기 때문에 중복도가 높고, Member는 계속해서 생성될 수 있기 때문에 중복도가 낮습니다.
 *
 */
@Embeddable
@EqualsAndHashCode
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberRoleId implements Serializable {

    private Member member;
    private Role role;

}
