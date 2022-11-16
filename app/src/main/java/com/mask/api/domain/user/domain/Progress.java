package com.mask.api.domain.user.domain;

import lombok.Builder;
import lombok.Data;

import java.util.HashSet;

@Data
@Builder
public class Progress {
    private HashSet<Integer> beginner;
    private HashSet<Integer> intermediate;
    private HashSet<Integer> advanced;
}
