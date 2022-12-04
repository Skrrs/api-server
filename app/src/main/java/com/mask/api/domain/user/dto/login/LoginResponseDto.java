package com.mask.api.domain.user.dto.login;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginResponseDto {
    private String token;
}
