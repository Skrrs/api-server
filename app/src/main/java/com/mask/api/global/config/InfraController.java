package com.mask.api.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InfraController {

    private final Environment env;

    @GetMapping("/health")
    public String health() {
        return "UP";
    }

    @GetMapping("/port")
    public String port() {
        return env.getProperty("local.server.port");
    }
}
