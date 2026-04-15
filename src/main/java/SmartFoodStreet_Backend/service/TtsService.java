package SmartFoodStreet_Backend.service;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.InputStream;

import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TtsService {

    private TextToSpeechClient client;

    @PostConstruct
    public void init() throws Exception {
        // Đường dẫn tới file trong máy (Downloads)
        String path = "C:/Users/X/Downloads/tts.json";

        InputStream credentialsStream = new FileInputStream(path);

        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);

        client = TextToSpeechClient.create(
                TextToSpeechSettings.newBuilder()
                        .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                        .build());
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

            SynthesizeSpeechResponse response = client.synthesizeSpeech(input, voice, audioConfig);

            return response.getAudioContent().toByteArray();

        } catch (Exception e) {
            log.error("TTS error: {}", e.getMessage());
            throw new RuntimeException("TTS_GENERATION_FAILED");
        }
    }
}