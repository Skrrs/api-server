package com.mask.api.domain.voice.service;

import com.mask.api.domain.voice.dto.ScoreDto;
import com.mask.api.domain.voice.dto.TestResultDto;
import com.mask.api.global.common.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class VoiceService {

    private final Response Response;

    @Value("${spring.ai.url}")
    public String url;

    public ResponseEntity<?> getAudioFile(MultipartFile audioFile, String answer) {

        ResponseEntity<TestResultDto> testResult = getTestResult(audioFile, answer);
        Double cer = Objects.requireNonNull(testResult.getBody()).getCer();
        int index = getIndexOfScore(cer);
        ScoreDto result = ScoreDto.builder().index(index).build();

        return Response.success(result, HttpStatus.OK);
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
        if(cer < 20)
            return 1;
        else if(cer < 40)
            return 2;
        else if(cer < 60)
            return 3;
        return 4;
    }

}
