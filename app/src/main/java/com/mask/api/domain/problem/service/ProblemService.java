package com.mask.api.domain.problem.service;

import com.mask.api.domain.problem.dao.ProblemRepository;
import com.mask.api.domain.problem.domain.Problem;
import com.mask.api.domain.problem.dto.ScoreDto;
import com.mask.api.domain.problem.dto.TestResultDto;
import com.mask.api.global.common.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProblemService {
    private final ProblemRepository problemRepository;
    private final Response response;


    @Value("${spring.ai.url}")
    private String url;


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


    public ResponseEntity<?> gradeProblem(MultipartFile audioFile, String answer) {

        ResponseEntity<TestResultDto> testResult = getTestResult(audioFile, answer);
        Double cer = Objects.requireNonNull(testResult.getBody()).getCer();
        int index = getIndexOfScore(cer);
        ScoreDto result = ScoreDto.builder().index(index).build();

        return response.success(result, HttpStatus.OK);
    }

    private ResponseEntity<TestResultDto> getTestResult(MultipartFile audioFile, String answer) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("audio_file", audioFile.getResource());
        body.add("answer", answer);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        var restTemplate = new RestTemplate();
        return restTemplate.exchange(url, HttpMethod.POST, requestEntity, TestResultDto.class);

    }

    private int getIndexOfScore(Double cer) {
        if(cer <= 10.0)
            return 1;
        else if(cer <= 20.0)
            return 2;
        return 3;
    }

}
