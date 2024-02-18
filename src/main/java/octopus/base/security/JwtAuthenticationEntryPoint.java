package octopus.base.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * 유효한 자격 증명을 제공하지 않고 접근하려 할 때 401 Unauthorized 에러 리턴
 * 인가(Authorization)가 실패했을 때 실행된다. 
 * 예를 들자면, 로그인 된 사용자가 필요한 요청에 로그인하지 않은 사용자가 접근했을 때 실행된다.
 * 
 * 출처 : https://www.bezkoder.com/spring-boot-jwt-authentication/
 * 출처 : https://velog.io/@u-nij/JWT-JWT-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0-3-JwtAuthenticationFilter-JwtAccessDeniedHandler-JwtAuthenticationEntryPoint
 * 출처 : https://velog.io/@gale4739/Spring-Security-%EC%A0%81%EC%9A%A9
 * </pre>
 * 
 * @author jypark
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        log.error("Unauthorized error: {}", authException.getMessage());
        
        //response.setCharacterEncoding("utf-8");
        //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "잘못된 접근입니다.");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 Status code

        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", authException.getMessage());
        body.put("path", request.getServletPath());

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}