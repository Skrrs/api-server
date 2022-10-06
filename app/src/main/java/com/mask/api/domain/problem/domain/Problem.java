package com.mask.api.domain.problem.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user")
@Data
@Builder
public class Problem {
    @Id
    private String id;

    private Integer idx;

    private String level;

    private String answer;

    private String pron;

    private String url;
}
