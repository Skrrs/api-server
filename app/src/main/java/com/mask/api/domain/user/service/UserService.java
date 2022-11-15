package com.mask.api.domain.user.service;

import com.mask.api.domain.user.dao.UserRepository;
import com.mask.api.domain.user.domain.Progress;
import com.mask.api.domain.user.domain.User;
import com.mask.api.domain.user.dto.LoginRequestDto;
import com.mask.api.domain.user.dto.LoginResponseDto;
import com.mask.api.domain.user.dto.LogoutRequestDto;
import com.mask.api.global.common.Response;
import com.mask.api.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final Response response;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()) throw new UsernameNotFoundException(email);

        return optionalUser.get();
    }

    public ResponseEntity<?> login(LoginRequestDto loginRequestDto ){
        if(!userRepository.existsByEmail(loginRequestDto.getEmail())){
            var authority = new HashSet<GrantedAuthority>();
            authority.add(new SimpleGrantedAuthority("ROLE_USER"));
            var progress = Progress.builder()
                    .advanced(0)
                    .intermediate(0)
                    .beginner(0)
                    .build();
            var newUser = User.builder()
                    .email(loginRequestDto.getEmail())
                    .calendar(null)
                    .progress(progress)
                    .library(null)
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
        redisTemplate.opsForValue()
                .set(user.getEmail(), token,JwtTokenProvider.ACCESS_TIME
                        , TimeUnit.SECONDS);
        log.info("NEW TOKEN CREATED {}",token);

        return response.success(responseDto,HttpStatus.OK);
    }
}
