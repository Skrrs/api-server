package com.mask.api.domain.user.service;

import com.mask.api.domain.user.dao.UserRepository;
import com.mask.api.domain.user.domain.User;
import com.mask.api.domain.user.dto.LoginRequestDto;
import com.mask.api.domain.user.dto.LoginResponseDto;
import com.mask.api.domain.user.dto.LogoutRequestDto;
import com.mask.api.global.custom.CustomResponse;
import com.mask.api.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CustomResponse customResponse;
    private final RedisTemplate<String, String> redisTemplate;

    public ResponseEntity<?> login(LoginRequestDto loginRequestDto ){
        if(!userRepository.existsByEmail(loginRequestDto.getEmail())){
            var authority = new HashSet<GrantedAuthority>();
            authority.add(new SimpleGrantedAuthority("ROLE_USER"));
            var newUser = User.builder()
                    .email(loginRequestDto.getEmail())
                    .authorities(authority)
                    .build();
            userRepository.save(newUser);
            log.info("NEW USER CREATED {} ",loginRequestDto.getEmail());
        }
        var user = userRepository.findByEmail(loginRequestDto.getEmail()).get();
        var token = JwtTokenProvider.generateToken(user);
        var responseDto = LoginResponseDto.builder()
                .token(token)
                .build();

        // Redis
        return customResponse.success(responseDto,HttpStatus.OK);
    }
    public ResponseEntity<?> logout(LogoutRequestDto logoutRequestDto){
        var responseDto = new Object();
        var accessTokenInfo = JwtTokenProvider.verify(logoutRequestDto.getToken());

        String isLogout = redisTemplate.opsForValue().get(logoutRequestDto.getToken());

        return customResponse.success(responseDto,HttpStatus.OK);
    }
}
