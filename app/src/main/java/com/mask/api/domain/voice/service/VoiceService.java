package com.mask.api.domain.voice.service;

import com.mask.api.domain.voice.dto.ScoreDto;
import com.mask.api.domain.voice.dto.TestResultDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.util.Objects;


@Service
public class VoiceService {

    @Value("${spring.ai.url}")
    public String url;

    public ResponseEntity<?> getAudioFile(MultipartFile audioFile, String answer) {

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("audio_file", audioFile.getResource());
        body.add("answer", answer);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();

        var testResult = restTemplate.exchange(url, HttpMethod.POST, requestEntity, TestResultDto.class);
        Double cer = Objects.requireNonNull(testResult.getBody()).getCer();
        int score = getScore(cer);
        ScoreDto result = ScoreDto.builder().result(score).build();

        return ResponseEntity.status(HttpStatus.OK).body(result);

    }

    private int getScore(Double cer) {
        if(cer < 20)
            return 1;
        else if(cer < 40)
            return 2;
        else if(cer < 60)
            return 3;
        return 4;
    }

}
