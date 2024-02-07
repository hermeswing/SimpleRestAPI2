package octopus.sample.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import octopus.base.service.ResponseManager;
import octopus.sample.dto.UserDTO;
import octopus.sample.entity.Users;
import octopus.sample.repository.UsersRepository;
import org.springframework.context.support.MessageSourceAccessor;
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
    private final ResponseManager responseManager;
    private final MessageSourceAccessor msgResource;

    @Transactional( readOnly = true )
    public ResponseEntity<?> existsByUserId( String userId ) {
        // return userRepository.existsByUserId( userId );
        return responseManager.getSingleResult( userRepository.existsByUserId( userId ) );
    }

    @Transactional( readOnly = true )
    public ResponseEntity<?> existsByUserNm( String userNm ) {
        // return userRepository.existsByUserNm( userNm );
        return responseManager.getSingleResult( userRepository.existsByUserNm( userNm ) );
    }

    @Transactional( readOnly = true )
    public ResponseEntity<?> existsByEmail( String email ) {
        // return userRepository.existsByEmail( email );
        return responseManager.getSingleResult( userRepository.existsByEmail( email ) );
    }

    @Transactional
    public ResponseEntity<?> findAll() {
        List<Users> users = userRepository.findAll();
        // return ResponseEntity.ok( UserDTO.UserDto.convertUsersToUserDtos( users ) );
        return responseManager.getListResult( UserDTO.UserDto.convertUsersToUserDtos( users ) );
    }

    @Transactional
    public ResponseEntity<?> findByUserId( String userId ) {
        Optional<Users> findUser = userRepository.findByUserId( userId );
        if( findUser.isEmpty() ) {
            // return ResponseEntity.badRequest().body( "데이터를 찾을 수 없습니다." );
            // return responseManager.getErrorResult(HttpStatus.BAD_REQUEST, "데이터를 찾을 수 없습니다.");
            return responseManager.getErrorResult( HttpStatus.BAD_REQUEST, msgResource.getMessage( "exception.dataNotFound" ) );  // 데이터를 찾을 수 없습니다.
        } else {
            // return ResponseEntity.ok( new UserDTO.UserDto( findUser.get() ) );
            UserDTO.UserDto dto = new UserDTO.UserDto( findUser.get() );
            log.debug( "dto :: {}", dto );
            return responseManager.getSingleResult( new UserDTO.UserDto( findUser.get() ) );
        }
    }

    @Transactional
    public ResponseEntity<?> registerUser( UserDTO.UserDto userDto ) {
        if( userRepository.existsByUserId( userDto.getUserId() ) ) {
            // return ResponseEntity.status( HttpStatus.CONFLICT ).body( "중복된 데이터가 존재합니다." );
            // return responseManager.getErrorResult( HttpStatus.CONFLICT, "중복된 데이터가 존재합니다." );
            return responseManager.getErrorResult( HttpStatus.CONFLICT, msgResource.getMessage( "exception.duplicateKey" ) );  // 중복된 데이터가 존재합니다.
        } else {
            Users users = Users.createEntry( userDto );
            Users saveUsers = userRepository.save( users );

            log.debug( "saveUsers :: {}", saveUsers );

            // return ResponseEntity.status( HttpStatus.CREATED ).body( "생성되었습니다." );
            // return responseManager.getSuccessResult(HttpStatus.CREATED, "생성되었습니다.");
            return responseManager.getSuccessResult( HttpStatus.CREATED, msgResource.getMessage( "msg.ok" ) );  // 정상처리되었습니다.
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

            // return ResponseEntity.ok( "수정되었습니다." );
            // return responseManager.getSuccessResult("수정되었습니다.");
            return responseManager.getSuccessResult( msgResource.getMessage( "msg.itIsChanged " ) );  // 수정되었습니다.
        } else {
            // return ResponseEntity.badRequest().body( "데이터를 찾을 수 없습니다." );
            return responseManager.getErrorResult( HttpStatus.BAD_REQUEST, msgResource.getMessage( "exception.dataNotFound" ) );  // 데이터를 찾을 수 없습니다.
        }
    }

    @Transactional
    public ResponseEntity<?> deleteByUserId( String userId ) {
        Optional<Users> findUser = userRepository.findByUserId( userId );
        if( findUser.isEmpty() ) {
            // return ResponseEntity.badRequest().body( "데이터를 찾을 수 없습니다." );
            return responseManager.getErrorResult( HttpStatus.BAD_REQUEST, msgResource.getMessage( "exception.dataNotFound" ) );  // 데이터를 찾을 수 없습니다.
        } else {
            userRepository.deleteByUserId( userId );
            // return ResponseEntity.ok( "삭제되었습니다." );
            // return responseManager.getSuccessResult("삭제되었습니다");
            return responseManager.getSuccessResult( msgResource.getMessage( "msg.itIsDeleted" ) );  // 삭제되었습니다
        }
    }
}
