package com.mask.api.domain.user.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user")
@Data
@Builder
public class User {
    @Id
    private String id;

    private String username;

    private String email;
}
