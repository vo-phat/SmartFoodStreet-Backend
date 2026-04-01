package SmartFoodStreet_Backend.controller;

import SmartFoodStreet_Backend.dto.tts.TtsRequest;
import SmartFoodStreet_Backend.service.TtsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tts")
@RequiredArgsConstructor
public class TtsController {

    private final TtsService ttsService;

    @PostMapping
    public ResponseEntity<byte[]> tts(@RequestBody TtsRequest request) {
        try {
            byte[] audio = ttsService.synthesize(request.getText());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"output.mp3\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(audio);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}