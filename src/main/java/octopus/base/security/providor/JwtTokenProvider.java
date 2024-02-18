package octopus.base.security.providor;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import octopus.base.WebConst;
import octopus.base.utils.DateUtils;
import octopus.base.utils.MyThreadLocal;
import octopus.login.dto.AuthDTO;
import octopus.login.dto.PrincipalDetails;
import octopus.login.service.PrincipalDetailsService;
import octopus.login.service.RedisService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * <pre>
 * 토큰의 생성, 토큰의 유효성 검증 등을 담당
 * 출처 : <a href="https://velog.io/@gale4739/Spring-Security-%EC%A0%81%EC%9A%A9">...</a>
 * https://github.com/u-nij/Authentication-Using-JWT
 * </pre>
 *
 * @author jypark
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional( readOnly = true )
public class JwtTokenProvider implements InitializingBean {

    private final PrincipalDetailsService userDetailsService;
    private final RedisService redisService;

    @Value( "${custom.jwt.secretKey}" )
    private String secretKey;

    @Value( "${custom.jwt.token-validity-in-seconds}" )
    private long tokenValidityInMilliseconds;

    @Value( "${custom.jwt.refresh-token-validity-in-seconds}" )
    private long refreshTokenValidityInMilliseconds;

    private Key signingKey;


    // Bean이 생성이 되고 주입을 받은 후에 secret값을 Base64로 Decode 해서 key 변수에 할당
    @Override
    public void afterPropertiesSet() {
        log.debug( "★★★★★★★★★★★★★★★★★★ [JwtTokenProvider.afterPropertiesSet] ★★★★★★★★★★★★★★★★★" );
        MyThreadLocal.setTrackingLog("[Provider] " + this.getClass().getName() + ".afterPropertiesSet()");

        byte[] keyBytes = Decoders.BASE64.decode( secretKey );
        this.signingKey = Keys.hmacShaKeyFor( keyBytes );

        //MyThreadLocal.setDevTrackingLog("[afterPropertiesSet] signingKey :: " + signingKey);

        log.debug( "[afterPropertiesSet] signingKey :: {}", signingKey );
    }

    // Authentication 객체의 권한 정보를 이용해서 토큰을 생성
    public String createToken( Authentication authentication ) {
        log.debug( "★★★★★★★★★★★★★★★★★★ [JwtTokenProvider.createToken] ★★★★★★★★★★★★★★★★★" );
        // authorities 설정
        String authorities = authentication.getAuthorities().stream()
                .map( GrantedAuthority::getAuthority )
                .collect( Collectors.joining( "," ) );

        log.debug( "[createToken] authentication :: {}", authorities );
        log.debug( "[createToken] authentication.getName() :: {}", authentication.getName() );

        Claims claims = Jwts.claims();
        claims.put( "username", authentication.getName() );

        // 토큰 만료 시간 설정
        long now = ( new Date() ).getTime();
        Date validity = new Date( now + this.tokenValidityInMilliseconds );

        return Jwts.builder()
                .setSubject( authentication.getName() )
                .claim( WebConst.AUTHORITIES_KEY, authorities )
                .setClaims( claims )
                .setIssuedAt( new Date() )
                .signWith( signingKey, SignatureAlgorithm.HS512 )
                .setExpiration( validity )
                .compact();
    }

    public String createToken2( String username, String userRole ) {
        log.debug( "[createToken2] username :: {}", username );

        // claims.getSubject() :: 사용자ID가 들어있음.
        Claims claims = Jwts.claims();
        claims.put( "username", username );

        return Jwts.builder()
                .setSubject( username )
                .claim( WebConst.AUTHORITIES_KEY, userRole )
                .setClaims( claims )
                .setIssuedAt( new Date() )
                .signWith( getSigninKey( secretKey ), SignatureAlgorithm.HS256 )
                .setExpiration( new Date( System.currentTimeMillis() + this.tokenValidityInMilliseconds ) )
                .compact();
    }

    /**
     * @param userId       사용자 Key ( USER_ID, EMAIL 등등 )
     * @param authorities 사용자 권한 ( ROLE_ADMIN, ROLE_USER 등등 )
     * @return AuthDTO.TokenDto 생성된 Token 정보
     */
    // @Transactional
    public AuthDTO.TokenDto createToken( String userId, String authorities ) {
        MyThreadLocal.setTrackingLog("[Provider] " + this.getClass().getName() + ".createToken(userId, authorities)");
        log.debug( "★★★★★★★★★★★★★★★★★★ [JwtTokenProvider.createToken] ★★★★★★★★★★★★★★★★★" );

        Long now = System.currentTimeMillis();

        MyThreadLocal.setDevTrackingLog("userId :: " + userId);
        //MyThreadLocal.setDevTrackingLog("authorities :: " + authorities);
        //MyThreadLocal.setDevTrackingLog("signingKey :: " + signingKey);

        log.debug( "[createToken] userId {}", userId );
        log.debug( "[createToken] authorities {}", authorities );
        log.debug( "[createToken] now :: {}", now );
        //log.debug( "[createToken] signingKey :: {}", signingKey );

        String accessToken = Jwts.builder()
                .setHeaderParam( "typ", "JWT" )
                .setHeaderParam( "alg", "HS512" )
                .setExpiration( new Date( now + tokenValidityInMilliseconds ) )
                .setSubject( "access-token" )
                .claim( WebConst.URL, true )
                .claim( WebConst.USER_ID, userId )
                .claim( WebConst.AUTHORITIES_KEY, authorities )
                .signWith( signingKey, SignatureAlgorithm.HS512 )
                .compact();

        String refreshToken = Jwts.builder()
                .setHeaderParam( "typ", "JWT" )
                .setHeaderParam( "alg", "HS512" )
                .setExpiration( new Date( now + refreshTokenValidityInMilliseconds ) )
                .setSubject( "refresh-token" )
                .signWith( signingKey, SignatureAlgorithm.HS512 )
                .compact();

        return new AuthDTO.TokenDto( accessToken, refreshToken );
    }

    // 토큰에 담겨있는 정보를 이용해 Authentication 객체 리턴
    // public Authentication getAuthentication(String token) {
    //
    // log.debug("[getAuthentication]");
    //
    // // 토큰을 이용하여 claim 생성
    // Claims claims = getClaims(token);
    //
    // // claim을 이용하여 authorities 생성
    // Collection<? extends GrantedAuthority> authorities = Arrays
    // .stream(claims.get(WebConst.AUTHORITIES_KEY).toString().split(","))
    // .map(SimpleGrantedAuthority::new)
    // .collect(Collectors.toList());
    // // claims.getSubject() :: 사용자ID가 들어있음.
    // // claim과 authorities 이용하여 User 객체 생성
    // User principal = new User(claims.getSubject(), "", authorities);
    //
    // // 최종적으로 Authentication 객체 리턴
    // return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    // }

    public Authentication getAuthentication( String token ) {
        String userId = getClaims( token ).get( WebConst.USER_ID ).toString();
        PrincipalDetails principal = userDetailsService.loadUserByUsername( userId );
        return new UsernamePasswordAuthenticationToken( principal, "", principal.getAuthorities() );
    }

    // 토큰 해석
    public Claims getClaims( String token ) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey( signingKey )
                    .build()
                    .parseClaimsJws( token )
                    .getBody();
        } catch( ExpiredJwtException e ) { // Access Token
            return e.getClaims();
        } catch( UnsupportedJwtException | MalformedJwtException
                 | IllegalArgumentException e ) {
            e.printStackTrace();
            throw new InvalidParameterException( "유효하지 않은 토큰입니다" );
        }
    }

    public String getClaimsValue( String token, String key ) {
        return getClaims(token).get( key ).toString();
    }

    public long getTokenExpirationTime( String token ) {
        return getClaims( token ).getExpiration().getTime();
    }

    // 유저id 조회
    public String getName( String token ) {
        return getClaims( token ).get( "username", String.class );
    }

    // 유저권한 조회
    public String[] getRoles( String token ) {
        Authentication authentication = getAuthentication( token );
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        authorities.stream().forEach( o -> log.debug( "Authority :: {}", o.getAuthority() ) );

        // authorities.stream().toArray();

        // authorities.stream().forEach(o -> o.getAuthority());
        return authorities.toArray( new String[authorities.size()] );
    }

    public boolean hasAdminRole( String token ) {
        Authentication authentication = getAuthentication( token );
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        authorities.stream().forEach( o -> log.debug( "Authority :: {}", o.getAuthority() ) );

        return authorities.stream().filter( o -> o.getAuthority().equals( "ROLE_ADMIN" ) ).findAny().isPresent();
    }

    private Key getSigninKey( String secretKey ) {
        byte[] keyBytes = secretKey.getBytes( StandardCharsets.UTF_8 );
        return Keys.hmacShaKeyFor( keyBytes );
    }

    // accessToken 유효시간 알림(second)
    public Long getValidationAccessTokenTime() {
        return tokenValidityInMilliseconds;
    }

    // 토큰 만료
    public Boolean isTokenExpired( String token ) {
        Date expiration = getClaims( token ).getExpiration();

        MyThreadLocal.setDevTrackingLog("만료시간 :: " + DateUtils.getDateFormat( expiration ));
        log.debug( "만료시간 :: {}", DateUtils.getDateFormat( expiration ) );

        return expiration.before( new Date() );
    }

    // Filter에서 사용
    public boolean validateAccessToken( String accessToken ) {
        try {
            //log.debug( "signingKey.getAlgorithm() :: {}", signingKey.getAlgorithm() );
            //log.debug( "signingKey.getEncoded() :: {}", signingKey.getEncoded() );
            log.debug( "isTokenExpired :: {}", isTokenExpired(accessToken) );
            String userId = getClaimsValue( accessToken, WebConst.USER_ID );
            //String value = redisService.getValues( accessToken );
            String value = redisService.getValues( "RT(" + WebConst.SERVER + "):" + userId  );

            log.debug( "value :: {}", value );

            if( value != null // NPE 방지
                    && value.equals( "logout" ) ) { // 로그아웃 했을 경우
                return false;
            }

            Jwts.parserBuilder()
                    .setSigningKey( signingKey )
                    .build()
                    .parseClaimsJws( accessToken );

            return true;
        } catch( ExpiredJwtException e ) {
            return true;
        } catch( Exception e ) {
            return false;
        }
    }

    // 토큰의 유효성 검증 수행
    public boolean validateRefreshToken( String refreshToken ) {
        try {
            if( redisService.getValues( refreshToken ).equals( "delete" ) ) { // 회원 탈퇴했을 경우
                return false;
            }
            Jwts.parserBuilder()
                    .setSigningKey( signingKey )
                    .build()
                    .parseClaimsJws( refreshToken );
            return true;
        } catch( SignatureException e ) {
            log.error( "Invalid JWT signature." );
        } catch( MalformedJwtException e ) {
            log.error( "Invalid JWT token." );
        } catch( ExpiredJwtException e ) {
            log.error( "Expired JWT token." );
        } catch( UnsupportedJwtException e ) {
            log.error( "Unsupported JWT token." );
        } catch( IllegalArgumentException e ) {
            log.error( "JWT claims string is empty." );
        } catch( NullPointerException e ) {
            log.error( "JWT Token is empty." );
        }
        return false;
    }
}
