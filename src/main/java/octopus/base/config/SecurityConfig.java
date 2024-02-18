package octopus.base.config;

import lombok.extern.slf4j.Slf4j;
import octopus.base.security.JwtAccessDeniedHandler;
import octopus.base.security.JwtAuthenticationEntryPoint;
import octopus.base.security.filter.JwtFilter;
import octopus.base.security.providor.JwtTokenProvider;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * <pre>
 * 개발환경
 * 1. build.gradle 추가
 * implementation 'org.springframework.boot:spring-boot-starter-security'
 *
 * TokenProvider, JwtFilter를 SecurityConfig에 적용할 떄 사용
 * Spring Security 5.70 이후부터 WebSecurityConfigurerAdapter를 상속 받는 방식은 deprecated
 * 참조 : <a href="https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter/">...</a>
 *
 * 출처 : <a href="https://velog.io/@u-nij/JWT-JWT-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0-1-Spring-Security">...</a>
 * 출처 : <a href="https://www.bezkoder.com/spring-boot-jwt-authentication/">...</a>
 * 출처 : <a href="https://velog.io/@gale4739/Spring-Security-%EC%A0%81%EC%9A%A9">...</a>
 * 출처: <a href="https://velog.io/@soyeon207/JWT-%EC%8B%A4%EC%8A%B5">...</a>
 * https://github.com/soyeon207/blog_example/blob/master/jwt-security-server/src/main/java/velog/soyeon/jwt/config/JwtAuthenticationFilter.java
 * </pre>
 *
 * @author jongyoung.park
 */
@Slf4j
@Configuration
@EnableWebSecurity // Spring Security 활성화 -> 기본 스프링 필터체인에 등록
@EnableGlobalMethodSecurity( securedEnabled = true )
// @EnableMethodSecurity // @PreAuthorize 어노테이션 메소드 단위로 추가하기 위해 적용 (default : true)
public class SecurityConfig {
    private final JwtTokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    // TokenProvider,JwtAuthenticationEntryPoint,JwtAccessDeniedHandler 의존성 주입
    public SecurityConfig( JwtTokenProvider tokenProvider, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, JwtAccessDeniedHandler jwtAccessDeniedHandler ) {
        log.debug( "★★★★★★★★★★★★★★★★★★ [SecurityConfig] 생성자 생성. ★★★★★★★★★★★★★★★★★" );
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    // 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 비밀번호를 DB에 저장하기 전 사용할 암호화
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        log.debug( "★★★★★★★★★★★★★★★★★★ [SecurityConfig.webSecurityCustomizer] ★★★★★★★★★★★★★★★★★" );
        // ACL(Access Control List, 접근 제어 목록)의 예외 URL 설정
        return ( web ) -> web.ignoring()
                // Spring Security should completely ignore URLs starting with /resources/
                // .requestMatchers("/resources/**");
                .requestMatchers( PathRequest.toStaticResources().atCommonLocations() );
    }

    @Bean
    public SecurityFilterChain securityFilterChain( HttpSecurity http ) throws Exception {
        log.debug( "★★★★★★★★★★★★★★★★★★ [SecurityConfig.securityFilterChain] ★★★★★★★★★★★★★★★★★" );

        http.csrf().disable() // 토큰을 사용하기 때문에 csrf 설정 disable
                .formLogin().disable() // 일반적인 로그인 방식, 즉 ID/Password 로그인 방식 사용을 의미한다. REST API 방식을 사용할 것이므로 사용하지 않는다.
                .httpBasic().disable() // Http basic Auth 기반의 로그인 인증 창. 비인증시 로그인폼 화면으로 리다이렉트한다. REST API 방식을 사용할 것이므로 사용하지 않는다.
                .addFilterBefore( new JwtFilter( tokenProvider ), UsernamePasswordAuthenticationFilter.class )
                // STATELESS로 세션 정책을 설정한다는 것은, 세션쿠키 방식의 인증 메커니즘 방식을 사용하지 않겠다는 것을 의미한다.
                // 인증에 성공한 이후라도 클라이언트가 다시 어떤 자원에 접근을 시도할 경우, SecurityContextPersistenceFilter는 세션 존재 여부를 무시하고
                // 항상 새로운 SecurityContext 객체를 생성하기 때문에 인증성공 당시 SecurityContext에 저장했던 Authentication 객체를 더 이상 참조 할 수 없게 된다.
                .sessionManagement().sessionCreationPolicy( SessionCreationPolicy.STATELESS ) // 세션 사용하지 않기 때문에 세션 설정 STATELESS

                // 예외 처리 시 직접 만들었던 클래스 추가
                // authenticationEntryPoint: 401 에러 핸들링을 위한 설정
                // accessDeniedHandler: 403 에러 핸들링을 위한 설정
                .and().exceptionHandling().authenticationEntryPoint( jwtAuthenticationEntryPoint ) // customEntryPoint
                .accessDeniedHandler( jwtAccessDeniedHandler ) // cutomAccessDeniedHandler

                // 토큰이 없는 상태에서 요청이 들어오는 API들은 permitAll
                // 페이지 권한 설정
                // anyRequest(): 그 외 나머지 리소스들을 의미한다.
                // authenticated(): 인증을 완료해야 접근을 허용한다.
                // hasRole("권한"): 특정 레벨의 권한을 가진 사용자만 접근을 허용한다.(SecurityContext에 저장했던 Authentication 객체의 Authorities를 검사한다.)
                // permitAll(): 인증 절차 없이 접근을 허용한다.
                .and().authorizeHttpRequests() // '인증'이 필요하다
                .requestMatchers( new AntPathRequestMatcher( "/login" ) ).permitAll().requestMatchers( new AntPathRequestMatcher( "/signup" ) ).permitAll().antMatchers( "/api/admin/**" ).hasRole( "ADMIN" ) // 관리자 페이지
                .antMatchers( "/**" ).authenticated() // 마이페이지 인증 필요
                //.anyRequest().permitAll()
                .and()
                // JwtFilter를 addFilterBefore로 등록했던 jwtSecurityConfig 클래스 적용
                .headers().frameOptions().sameOrigin();

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager( AuthenticationConfiguration authenticationConfiguration ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}