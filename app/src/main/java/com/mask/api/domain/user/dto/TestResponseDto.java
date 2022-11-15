package com.mask.api.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
public class TestResponseDto {
    private List<Integer> index;

    private List<String> sentence;

    private List<String> voiceUrl;

    private List<String> pron;

    private List<String> english;
}
