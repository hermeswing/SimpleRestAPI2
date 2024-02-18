package octopus.login.dto;

import octopus.sample.entity.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/**
 * <pre>
 * Spring Security가 /login.do 요청이 들어오면 로그인이 완료되면 Security Session 을 생성한다. 
 * ( SecurityHolder ) Object Type => Authentication 타입 객체 Authentication 안에 User 정보가 있어야 함. 
 * User Object Type => UserDetails Type 객체 
 * Security Session -> Authentication -> UserDetails(PrincipalDetails)
 * </pre>
 */
@SuppressWarnings("serial")
public class PrincipalDetails implements UserDetails {
    
    private Users user;
    
    public PrincipalDetails( Users user) {
        this.user = user;
    }
    
    public Users getUser() {
        return user;
    }
    
    @Override
    public String getPassword() {
        return user.getPassword();
    }
    
    @Override
    public String getUsername() {
        return user.getEmail();
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authList = new ArrayList<>();
        // authList.add(new SimpleGrantedAuthority(user.getUserRole().getKey()));
        // return authList;
        authList.add(() -> user.getUserRole().getKey()); // key: ROLE_권한
        return authList;
    }
    
    // 계정의 만료 여부
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    // 계정의 잠김 여부
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    // 비밀번호 만료 여부
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    // 계정의 활성화 여부
    @Override
    public boolean isEnabled() {
        return true;
    }
}