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

    // 회원가입
    //@PostMapping( "/signup", produces = MediaType.APPLICATION_JSON_UTF8_VALUE ) Spring 5.2 이하에서 사용.
    @PostMapping( value="/signup", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<?> signup( @Valid @RequestBody UserDTO.UserDto userDTO ) {
        return userService.registerUser( userDTO );
    }

    @GetMapping( value="/findByUserId/{userId}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<?> findByUserId(@PathVariable String userId) {
        return userService.findByUserId(userId);
    }
    @GetMapping( value="/findAll", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<?> findAll() {
        return userService.findAll();
    }
}