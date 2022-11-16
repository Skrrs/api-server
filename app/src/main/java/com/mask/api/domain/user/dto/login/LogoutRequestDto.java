package com.mask.api.domain.user.dto.login;

import lombok.Getter;

@Getter
public class LogoutRequestDto {
    private String email;
    private String token;
}
