package com.mask.api.global.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class VerifyResult {
    private boolean success;
    private String email;
}
