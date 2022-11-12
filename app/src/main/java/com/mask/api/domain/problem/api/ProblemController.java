package com.mask.api.domain.problem.api;

import com.mask.api.domain.problem.dto.ProblemRequestDto;
import com.mask.api.domain.problem.service.ProblemService;
import com.mask.api.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/problem")
@RequiredArgsConstructor //DI
public class ProblemController  {
    private final ProblemService problemService;

    @PostMapping("/grade")
    public ResponseEntity<?> gradeProblem(@RequestBody ProblemRequestDto problemRequestDto) {
        return problemService.gradeProblem(problemRequestDto);
    }
}