package octopus.base.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <pre>
 * DB에 ROLE_이라는 접두어를 붙여 저장하고 싶지 않을 때 이처럼 사용할 수 있다.
 * 출처 : https://velog.io/@u-nij/JWT-JWT-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0-2-User-Role-UserDetailsImpl-UserdetailsServiceImpl
 * </pre>
 *
 * @author jypark
 */
@AllArgsConstructor
@Getter
public enum UserRole {
    ADMIN( "ROLE_ADMIN", "관리자" ),
    USER( "ROLE_USER", "일반 사용자" );

    private final String key;
    private final String title;
}