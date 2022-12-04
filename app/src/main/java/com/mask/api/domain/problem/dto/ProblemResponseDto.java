package com.mask.api.domain.problem.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ProblemResponseDto {
    private List<Integer> index;

    private List<String> sentence;

    private List<String> voiceUrl;

    private List<String> pron;

    private List<String> english;
}
