package com.mask.api.domain.user.service;

import com.mask.api.domain.user.dao.UserRepository;
import com.mask.api.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public ResponseEntity<?> login (String name){
        var user = User.builder()
                        .username(name)
                        .email(name)
                        .build();

        userRepository.save(user);
        return new ResponseEntity<>(name, HttpStatus.ACCEPTED);
    }
}
