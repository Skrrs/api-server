package com.mask.api.domain.user.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Progress {
    private Integer beginner;
    private Integer intermediate;
    private Integer advanced;
}
