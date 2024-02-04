package octopus.sample.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import octopus.sample.dto.UserDTO;
import octopus.sample.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    @PostMapping( "/signup" )
    public ResponseEntity<?> signup( @Valid @RequestBody UserDTO.UserDto userDTO ) {
        return userService.registerUser( userDTO );
    }

    @GetMapping( "/findAll" )
    public ResponseEntity<?> findAll() {
        return userService.findAll();
    }
}