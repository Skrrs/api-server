package com.mask.api.domain.user.api;


import com.mask.api.domain.user.dto.favorite.FavoriteRequestDto;
import com.mask.api.domain.user.dto.login.LoginRequestDto;
import com.mask.api.domain.user.dto.login.LogoutRequestDto;
import com.mask.api.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/user/{email}/test/{level}")
    public ResponseEntity<?> requestTest(@PathVariable(name="email")String email,
                                         @PathVariable(name="level")Integer level){
        return userService.requestTest(email, level);
    }

    @PostMapping("/user/{email}/fav/{level}")
    public ResponseEntity<?> favoriteAdd(@PathVariable(name="email")String email,
                                         @PathVariable(name="level")Integer level,
                                         @RequestBody FavoriteRequestDto favoriteRequestDto){
        return userService.favoriteAdd(email,level,favoriteRequestDto);
    }
}
