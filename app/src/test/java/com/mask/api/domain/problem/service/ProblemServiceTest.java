package com.mask.api.domain.problem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;


@SpringBootTest
class ProblemServiceTest {

    @Test
    void gradeProblem() {
    }


    @Autowired
    private ProblemService problemService;

    @Autowired
    private ObjectMapper objectMapper;

    private ClientAndServer mockServer;

    static String aiServerResult = "{\"cer\": 10.0, \"recognized_text\": \"sample text\"}";

    @BeforeEach
    void setUp(){
        mockServer = ClientAndServer.startClientAndServer(5022);
        new MockServerClient("localhost", 5022)
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/api/ai/model/conformer")
                )
                .respond(
                        response()
                                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                                .withBody(aiServerResult)
                );
    }
    @AfterEach
    void shutDown(){
        mockServer.stop();
    }


    @Test
    @DisplayName("음성 인식 기능 테스트")
    void getAudioFile() throws IOException {
        System.out.println("음성 인식 기능 테스트 시작");
        File file = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "sample.wav");

        FileInputStream fi1 = new FileInputStream(file);
        MockMultipartFile multipartFile = new MockMultipartFile("audioFile", file.getName(), "multipart/form-data", fi1);
        String answer = "테스트 파일입니다.";

        var actualResponse = problemService.gradeProblem(multipartFile, answer);
        String expected = "{\"result\":{\"index\":1},\"message\":\"Success\"}";

        assertNotNull(actualResponse);

        String actual = objectMapper.writeValueAsString(actualResponse.getBody());

        assertEquals(expected, actual);
    }


}


