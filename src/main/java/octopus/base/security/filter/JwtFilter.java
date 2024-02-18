package octopus.base.security.filter;

import lombok.extern.slf4j.Slf4j;
import octopus.base.WebConst;
import octopus.base.security.providor.JwtTokenProvider;
import octopus.base.utils.MyThreadLocal;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <pre>
 * JWT를 위한 커스텀 필터
 * 출처 : https://velog.io/@u-nij/JWT-JWT-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0-3-JwtAuthenticationFilter-JwtAccessDeniedHandler-JwtAuthenticationEntryPoint
 * 출처 : https://velog.io/@gale4739/Spring-Security-%EC%A0%81%EC%9A%A9
 * </pre>
 *
 * @author jypark
 */
@Slf4j
// public class JwtFilter extends GenericFilterBean {
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    public JwtFilter( JwtTokenProvider tokenProvider) {
        log.debug("★★★★★★★★★★★★★★★★★★ [JwtFilter] JwtTokenProvider Inject ★★★★★★★★★★★★★★★★★");
        this.tokenProvider = tokenProvider;
    }

    /**
     * <pre>
     *     MDC 의 Thread ID 가 생성되는 시작 지점.
     *     doFilter : 토큰의 인증 정보를 SecurityContext에 저장
     * </pre>
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    // public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
    // ServletException {
    protected void doFilterInternal (
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String threadId = "ThreadId-" + Thread.currentThread().getId();
        MDC.put( WebConst.THREAD_ID, threadId );
        // ThreadLocal 을 초기화 한다.
        MyThreadLocal.clearContext();
        MyThreadLocal.setContext( WebConst.THREAD_ID, threadId );
        MyThreadLocal.setTrackingLog( "[Filter Call] " + this.getClass().getName() + ".doFilterInternal(request, response, filterChain)" );

        log.debug("Thread ID 를 Log에 사용하기 시작합니다.");

        // resolveToken을 통해 토큰을 받아옴
        String accessToken = resolveToken(request);
        HttpServletRequest httpServletRequest = request;
        String requestURI = httpServletRequest.getRequestURI();

        log.debug("[JwFilter.doFilterInternal] [resolveToken] 클라이언트에서 받아온 accessToken : {}", accessToken);

        MyThreadLocal.setDevTrackingLog("[JwFilter.doFilterInternal] requestURI :: " + requestURI);
        MyThreadLocal.setDevTrackingLog("[JwFilter.doFilterInternal] 클라이언트에서 받아온 Access Token :: " + accessToken);

        // 토큰 유효성 검증 후 정상이면 SecurityContext에 저장
        if (StringUtils.hasText(accessToken) && tokenProvider.validateAccessToken(accessToken)) {
            Authentication authentication = tokenProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            MyThreadLocal.setDevTrackingLog("[JwFilter.doFilterInternal] Security Context에 '" + authentication.getName() + "' 인증 정보를 저장했습니다, URi :: " + requestURI);
        } else {
            log.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
        }

        // 생성한 필터 실행
        // chain.doFilter(httpServletRequest, response);
        filterChain.doFilter(request, response);

        MyThreadLocal.setTrackingLog( "[Filter Release] " + this.getClass().getName() + ".doFilterInternal(request, response, filterChain)" );
    }

    // Request Header에서 토큰 정보를 꺼내오기
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(WebConst.AUTHORIZATION_HEADER);

        // if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            //log.debug("[resolveToken] Header정보 > Bearer token : {}", bearerToken);

            return bearerToken.substring(7);
        }

        return null;
    }
}