package octopus.sample.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import octopus.sample.dto.UserDTO;
import octopus.sample.entity.Users;
import octopus.sample.repository.UsersRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * <pre>
 * 출처 : <a href="https://velog.io/@u-nij/JWT-JWT-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0-4-Redis-%EC%84%A4%EC%A0%95RedisRepositoryConfig-RedisService">...</a>
 * </pre>
 *
 * @author jypark
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional( readOnly = true )
public class UserService {
    private final UsersRepository userRepository;

    @Transactional( readOnly = true )
    public boolean existsByUserId( String userId ) {
        return userRepository.existsByUserId( userId );
    }

    @Transactional( readOnly = true )
    public boolean existsByUserNm( String userNm ) {
        return userRepository.existsByUserNm( userNm );
    }

    @Transactional( readOnly = true )
    public boolean existsByEmail( String email ) {
        return userRepository.existsByEmail( email );
    }

    @Transactional
    public ResponseEntity<?> findAll() {
        List<Users> users = userRepository.findAll();
        return ResponseEntity.ok( UserDTO.UserDto.convertUsersToUserDtos( users ) );
    }

    @Transactional
    public ResponseEntity<?> findByUserId( String userId ) {
        Optional<Users> findUser = userRepository.findByUserId( userId );
        if( findUser.isEmpty() ) {
            return ResponseEntity.badRequest().body( "데이터를 찾을 수 없습니다." );
        } else {
            return ResponseEntity.ok( new UserDTO.UserDto( findUser.get() ) );
        }
    }

    @Transactional
    public ResponseEntity<?> registerUser( UserDTO.UserDto userDto ) {
        if( userRepository.existsByUserId( userDto.getUserId() ) ) {
            return ResponseEntity.status( HttpStatus.CONFLICT ).body( "중복된 데이터가 존재합니다." );
        } else {
            Users users = Users.createEntry( userDto );
            Users saveUsers = userRepository.save( users );

            log.debug( "saveUsers :: {}", saveUsers );

            return ResponseEntity.status( HttpStatus.CREATED ).body( "생성되었습니다." );
        }
    }

    @Transactional
    public ResponseEntity<?> updateByUserId( UserDTO.UserDto userDto ) {
        Optional<Users> existingUser = userRepository.findByUserId( userDto.getUserId() );

        // 존재여부 확인
        if( existingUser.isPresent() ) {
            Users users = existingUser.get();
            users.updateUsers( userDto );
            userRepository.save( users );

            return ResponseEntity.ok( "수정 되었습니다." );
        } else {
            return ResponseEntity.badRequest().body( "데이터를 찾을 수 없습니다." );
        }
    }

    @Transactional
    public ResponseEntity<?> deleteByUserId( String userId ) {
        Optional<Users> findUser = userRepository.findByUserId( userId );
        if( findUser.isEmpty() ) {
            return ResponseEntity.badRequest().body( "데이터를 찾을 수 없습니다." );
        } else {
            userRepository.deleteByUserId( userId );
            return ResponseEntity.ok( "삭제되었습니다." );
        }
    }
}
