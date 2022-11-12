package com.mask.api.domain.problem.service;

import com.mask.api.domain.problem.dao.ProblemRepository;
import com.mask.api.domain.problem.dto.ProblemRequestDto;
import com.mask.api.domain.problem.dto.ProblemResponseDto;
import com.mask.api.domain.user.dto.LoginRequestDto;
import com.mask.api.domain.user.dto.LoginResponseDto;
import com.mask.api.global.custom.CustomResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProblemService {
    private final ProblemRepository problemRepository;
    private final CustomResponse customResponse;

    public ResponseEntity<?> gradeProblem(ProblemRequestDto problemRequestDto){
        boolean result = true;
        MultipartFile voiceFile = problemRequestDto.getRecord();

        if(!voiceFile.isEmpty()){
            log.info("voice file accepted {}",voiceFile);
        }else {
            log.error("voice file accepte failed");
        }

        var responseDto = ProblemResponseDto.builder()
                .result(result)
                .build();

        return customResponse.success(responseDto, HttpStatus.OK);
    }
}
