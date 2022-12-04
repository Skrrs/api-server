package com.mask.api.global.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder
@Getter
public class CustomError {
    private final String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    private String code;
    private String error;
}
