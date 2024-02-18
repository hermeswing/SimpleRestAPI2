package octopus.base.config;

import lombok.extern.slf4j.Slf4j;
import octopus.base.security.filter.JwtFilter;
import octopus.base.security.providor.JwtTokenProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * <pre>
 * TokenProvider, JwtFilter를 SecurityConfig에 적용할 떄 사용
 * SecurityConfigurerAdapter를 extends
 * 출처 : <a href="https://velog.io/@gale4739/Spring-Security-%EC%A0%81%EC%9A%A9">...</a>
 * </pre>
 *
 * @author jypark
 */
@Slf4j
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final JwtTokenProvider tokenProvider;

    // TokenProvider를 주입
    public JwtSecurityConfig( JwtTokenProvider tokenProvider ) {
        log.debug( "★★★★★★★★★★★★★★★★★★ [JwtSecurityConfig 생성자] ★★★★★★★★★★★★★★★★★" );
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void configure( HttpSecurity http ) {
        log.debug( "★★★★★★★★★★★★★★★★★★ [JwtSecurityConfig.configure] ★★★★★★★★★★★★★★★★★" );

        JwtFilter customFilter = new JwtFilter( tokenProvider );
        http.addFilterBefore( customFilter, UsernamePasswordAuthenticationFilter.class );
    }
}