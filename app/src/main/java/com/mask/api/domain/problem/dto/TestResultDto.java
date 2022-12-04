package com.mask.api.domain.problem.dto;

import lombok.*;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@RequiredArgsConstructor
@AllArgsConstructor
public class TestResultDto {

    private Double cer;
    private String recognized_text;

}
