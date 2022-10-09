package com.mask.api.domain.user.api;


import com.mask.api.domain.user.dto.LoginRequestDto;
import com.mask.api.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor //DI
public class UserController {
    private final UserService userService; //DI
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto){
//        return userService.login(loginRequestDto);
//    }
}
