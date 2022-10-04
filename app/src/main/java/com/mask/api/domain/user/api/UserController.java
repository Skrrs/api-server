package com.mask.api.domain.user.api;


import com.mask.api.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor //DI
public class UserController {
    private final UserService userService; //DI

    @GetMapping("login/{name}")
    public ResponseEntity<?> login(@PathVariable String name){
        System.out.println("test" + name);
        return userService.login(name);
    }
}
