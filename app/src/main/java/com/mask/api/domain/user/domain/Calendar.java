package com.mask.api.domain.user.domain;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;


@Data
@Builder
public class Calendar {
    private LocalDate date;
    private Integer attendance;
}
