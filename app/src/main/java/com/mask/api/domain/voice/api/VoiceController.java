package com.mask.api.domain.voice.api;


import com.mask.api.domain.voice.service.VoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/voice")
@RequiredArgsConstructor
public class VoiceController {

    private final VoiceService voiceService;


    @PostMapping("/")
    public ResponseEntity<?> getAudioFile(@ModelAttribute MultipartFile audioFile, String answer) {
        return voiceService.getAudioFile(audioFile, answer);
    }


}
