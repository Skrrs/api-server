package com.mask.api.domain.problem.service;

import com.mask.api.domain.problem.dao.ProblemRepository;
import com.mask.api.domain.problem.domain.Problem;
import com.mask.api.global.common.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProblemService {
    private final ProblemRepository problemRepository;
    private final Response response;

    public void insertProblem(){
        ClassPathResource resource = new ClassPathResource("problem_DB");
        try{
            InputStream stream = new BufferedInputStream(resource.getInputStream());
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(stream) 
            );
            List<String> lines = new ArrayList<>();
            String line;
            while((line = reader.readLine()) != null){
                lines.add(line);
            }
            lines.forEach(
                    info->{
                        String[] parsed = info.split("\\|");
                        var problem = Problem.builder()
                                .idx(Integer.parseInt(parsed[0]))
                                .level(parsed[1])
                                .answer(parsed[2])
                                .pron(parsed[3])
                                .english(parsed[4])
                                .url(parsed[5])
                                .build();
                        problemRepository.save(problem);
                    }
            );
        }catch(Exception e){
            e.printStackTrace();
            log.error("Update problem_DB Error.");
        }
    }
}
