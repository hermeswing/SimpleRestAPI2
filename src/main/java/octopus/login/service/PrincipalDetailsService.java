package octopus.login.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import octopus.base.exception.ExUserNotFoundException;
import octopus.base.utils.MyThreadLocal;
import octopus.login.dto.PrincipalDetails;
import octopus.sample.entity.Users;
import octopus.sample.repository.UsersRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Optional;

// UserDetailsService는 IoC로 찾음
@Slf4j
@Component
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UsersRepository userRepository;

    @Override
    public PrincipalDetails loadUserByUsername( String userId ) {
        boolean existsYn = userRepository.existsByUserId( userId );
        log.debug( "userId :: [" + userId + "] 사용자는 존재해? :: {}", existsYn );
        MyThreadLocal.setDevTrackingLog( "userId :: [" + userId + "] 사용자는 존재해? :: " + existsYn );

        if( existsYn ) {
            Optional<Users> findUser = userRepository.findByUserId( userId );
            MyThreadLocal.setDevTrackingLog( "조회된 사용자 정보 :: " + findUser.get() );

            return new PrincipalDetails( findUser.get() );
        } else {
            throw new ExUserNotFoundException( "Can't find user with this User ID. -> " + userId );
        }
    }

}
