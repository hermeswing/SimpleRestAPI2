package octopus.sample.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import octopus.sample.dto.UserDTO;
import octopus.sample.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;

    /**
     * <pre>사용자 ID 에 해당 하는 사용자를 조회한다.</pre>
     * @param userId 조회할 사용자 ID
     * @return 조회된 사용자 정보
     */
    @GetMapping( value="/findByUserId/{userId}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<?> findByUserId(@PathVariable String userId) {
        return userService.findByUserId(userId);
    }

    /**
     * <pre>사용자 정보 전체를 조회한다.</pre>
     * @return 사용자 정로를 List로 반환한다.
     */
    @GetMapping( value="/findAll", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<?> findAll() {
        return userService.findAll();
    }

    /**
     * <pre>사용자 가입</pre>
     * @param userDTO 가입할 사용자 정보
     * @return 가입결과
     */
    //@PostMapping( "/signup", produces = MediaType.APPLICATION_JSON_UTF8_VALUE ) Spring 5.2 이하에서 사용.
    @PostMapping( value="/signup", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<?> signup( @Valid @RequestBody UserDTO.UserDto userDTO ) {
        return userService.registerUser( userDTO );
    }

    /**
     * <pre>사용자 정보 수정</pre>
     * @param userDTO 수정할 사용자 정보
     * @return 수정결과
     */
    //@PostMapping( "/signup", produces = MediaType.APPLICATION_JSON_UTF8_VALUE ) Spring 5.2 이하에서 사용.
    @PostMapping( value="/updateByUserId", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<?> updateByUserId( @Valid @RequestBody UserDTO.UserDto userDTO ) {
        return userService.updateByUserId( userDTO );
    }

    /**
     *
     * @param userId 삭제된 사용자 아이디
     * @return 결과메시지
     */
    @GetMapping( value="/deleteByUserId/{userId}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<?> deleteByUserId(@PathVariable String userId) {
        return userService.deleteByUserId(userId);
    }
}