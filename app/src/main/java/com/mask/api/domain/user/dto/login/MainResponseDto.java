package com.mask.api.domain.user.dto.login;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MainResponseDto {
    private Integer beginner;
    private Integer intermediate;
    private Integer advanced;
    private Integer attendance;
}
