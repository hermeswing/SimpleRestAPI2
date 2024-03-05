package octopus.login.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import octopus.base.WebConst;
import octopus.base.security.providor.JwtTokenProvider;
import octopus.base.service.ResponseManager;
import octopus.login.dto.AuthDTO;
import octopus.login.service.AuthService;
import octopus.sample.dto.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final ResponseManager responseService;

    @Value( "${custom.jwt.cookie-expiration}" )
    private long cookieExpiration;

    /**
     * <pre>
     * 로그인 -> 토큰 발급
     * </pre>
     *
     * @param loginDto
     * @return
     */
    @PostMapping( "/login" )
    @Operation(summary = "회원 조회", description = "id에 해당하는 회원을 조회한다.", responses = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "실패 - id에 해당하는 회원이 없음", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    //public ResponseEntity<?> login(@RequestBody @Valid AuthDTO.LoginDto loginDto, HttpServletResponse response) {
    public ResponseEntity login( @RequestBody @Valid AuthDTO.LoginDto loginDto, HttpServletResponse response ) {
        log.debug( "★★★★★★★★★★★★★★★★★★ [/login] ★★★★★★★★★★★★★★★★★" );
        // User 등록 및 Refresh Token 저장
        AuthDTO.TokenDto tokenDto = authService.login( loginDto );

        ResponseCookie cookie = ResponseCookie.from( "refresh-token", tokenDto.getRefreshToken() )
                .httpOnly( true ).maxAge( cookieExpiration ).build();

        // RT 저장
        // HttpCookie httpCookie = ResponseCookie.from("refresh-token", tokenDto.getRefreshToken())
        // .maxAge(cookieExpiration)
        // .httpOnly(true)
        // .secure(true)
        // .build();

        return ResponseEntity.ok().header( HttpHeaders.SET_COOKIE, cookie.toString() )
                // AT 저장
                .header( HttpHeaders.AUTHORIZATION, "Bearer " + tokenDto.getAccessToken() ).body( responseService.getSuccessResult() );
    }

    @PostMapping( "/login2" )
    public String getLoginToken( @RequestBody UserDTO.UserDto loginRequestDTO ) {

        log.debug( "Email :: {}", loginRequestDTO.getEmail() );
        log.debug( "Password :: {}", loginRequestDTO.getPassword() );

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken( loginRequestDTO.getEmail(), loginRequestDTO.getPassword() );

        log.debug( "authenticationToken :: {}", authenticationToken );

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate( authenticationToken );
        SecurityContextHolder.getContext().setAuthentication( authentication );

        String jwt = tokenProvider.createToken( authentication );

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add( WebConst.AUTHORIZATION_HEADER, "Bearer " + jwt );

        return jwt;
    }

}