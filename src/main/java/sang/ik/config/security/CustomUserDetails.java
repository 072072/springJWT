package sang.ik.config.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

//CustomUserDetails는 인증된 사용자의 정보와 권한을 담고 있습니다.
//Spring Security에서 제공해주는 UserDetails 인터페이스를 구현한 클래스
@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final String userId;
    private final Set<GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return authorities;
    }

    @Override
    public String getUsername(){
        return userId;
    }

    @Override
    public String getPassword(){
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAccountNonExpired(){
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAccountNonLocked(){
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCredentialsNonExpired(){
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEnabled(){
        throw new UnsupportedOperationException();
    }
}
