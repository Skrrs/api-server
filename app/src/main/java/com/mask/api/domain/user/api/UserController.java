package com.mask.api.domain.user.api;


import com.mask.api.domain.user.dto.LoginRequestDto;
import com.mask.api.domain.user.dto.LogoutRequestDto;
import com.mask.api.domain.user.service.UserService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor //DI
public class UserController {
    private final UserService userService;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto){
        return userService.login(loginRequestDto);
    }

    @PostMapping("/user/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequestDto logoutRequestDto){
        return userService.logout(logoutRequestDto);
    }
}
