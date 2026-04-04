package SmartFoodStreet_Backend.service;

import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TtsService {

    private TextToSpeechClient client;

    @PostConstruct
    public void init() throws Exception {
        client = TextToSpeechClient.create();
    }

    /**
     * Generate MP3 từ text (Đã bổ sung voiceName để giọng đọc chuẩn xác)
     */
    public byte[] generate(String text, String languageCode, String voiceName) {
        try {
            SynthesisInput input = SynthesisInput.newBuilder()
                    .setText(text)
                    .build();

            // CẤU HÌNH GIỌNG ĐỌC CỤ THỂ
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode(languageCode)
                    .setName(voiceName)
                    .build();

            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .build();

            SynthesizeSpeechResponse response =
                    client.synthesizeSpeech(input, voice, audioConfig);

            return response.getAudioContent().toByteArray();

        } catch (Exception e) {
            log.error("TTS error: {}", e.getMessage());
            throw new RuntimeException("TTS_GENERATION_FAILED");
        }
    }
}