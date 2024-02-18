package octopus.login.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * 출처 : https://velog.io/@u-nij/JWT-JWT-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0-4-Redis-%EC%84%A4%EC%A0%95RedisRepositoryConfig-RedisService
 * </pre>
 *
 * @author jypark
 */
public class AuthDTO {
    @Getter
    @NoArgsConstructor( access = AccessLevel.PROTECTED )
    public static class LoginDto {
        private String userId;
        private String password;

        @Builder
        public LoginDto( String userId, String password ) {
            this.userId = userId;
            this.password = password;
        }
    }

    @Getter
    @NoArgsConstructor( access = AccessLevel.PROTECTED )
    public static class SignupDto {
        private String userId;
        private String password;

        @Builder
        public SignupDto( String userId, String password ) {
            this.userId = userId;
            this.password = password;
        }

        public static SignupDto encodePassword( SignupDto signupDto, String encodedPassword ) {
            SignupDto newSignupDto = new SignupDto();
            newSignupDto.userId = signupDto.getUserId();
            newSignupDto.password = encodedPassword;
            return newSignupDto;
        }
    }

    @Getter
    @NoArgsConstructor( access = AccessLevel.PROTECTED )
    public static class TokenDto {
        private String accessToken;
        private String refreshToken;

        public TokenDto( String accessToken, String refreshToken ) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }
}
