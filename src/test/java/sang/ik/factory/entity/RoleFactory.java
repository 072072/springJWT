package sang.ik.factory.entity;

import sang.ik.entity.member.Role;
import sang.ik.entity.member.RoleType;

public class RoleFactory {

    public static Role createRole(){
        return new Role(RoleType.ROLE_NORMAL);
    }
}
