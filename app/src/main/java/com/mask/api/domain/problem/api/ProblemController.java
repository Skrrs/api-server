package com.mask.api.domain.problem.api;

import com.mask.api.domain.problem.service.ProblemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/problem")
@RequiredArgsConstructor //DI
@Api( tags = "Problem")
public class ProblemController  {
    private final ProblemService problemService;

    @Operation(summary = "채점 API", description = "음성파일을 토대로 ai-server에서 채점후 결과 제공.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "요청 성공."),
            @ApiResponse(code = 401, message = "인증 에러."),
            @ApiResponse(code = 403, message = "토큰 권한 에러."),
            @ApiResponse(code = 500, message = "서버 내부 에러.")
    })
    @PostMapping("/grade")
    public ResponseEntity<?> gradeProblem(@RequestPart MultipartFile file, String answer) {
        return problemService.gradeProblem(file, answer);
    }

    @Operation(summary = "DB 삽입 API", description = "최초 DB 데이터 삽입 용도.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "요청 성공."),
            @ApiResponse(code = 500, message = "서버 내부 에러.")
    })
    @GetMapping("/insert")
    public String insertProblem(){
        problemService.insertProblem();
        return "Done";
    }
}