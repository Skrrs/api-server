package com.mask.api.domain.user.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Library {
    private String id;
    private String pid;
}
