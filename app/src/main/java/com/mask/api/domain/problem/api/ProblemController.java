package com.mask.api.domain.problem.api;

import com.mask.api.domain.problem.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/problem")
@RequiredArgsConstructor //DI
public class ProblemController  {
    private final ProblemService problemService;

//    @PostMapping("/grade")
//    public ResponseEntity<?> gradeProblem(@RequestPart MultipartFile file) {
//        return problemService.gradeProblem(file);
//    }
    @GetMapping("/insert")
    public String insertProblem(){
        problemService.insertProblem();
        return "Done";
    }
}