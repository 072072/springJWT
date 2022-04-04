package sang.ik.repository.role;

import org.springframework.data.jpa.repository.JpaRepository;
import sang.ik.entity.member.Role;
import sang.ik.entity.member.RoleType;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleType(RoleType roleType);
}
