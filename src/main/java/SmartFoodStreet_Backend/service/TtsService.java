package SmartFoodStreet_Backend.service;

import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;

@Service
public class TtsService {

    public byte[] synthesize(String text) throws Exception {

        try (TextToSpeechClient client = TextToSpeechClient.create()) {

            // Input text
            SynthesisInput input = SynthesisInput.newBuilder()
                    .setText(text)
                    .build();

            // Voice (tiếng Việt)
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("vi-VN")
                    .setName("vi-VN-Wavenet-A")
                    .build();

            // Audio config
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .build();

            // Call API
            SynthesizeSpeechResponse response = client.synthesizeSpeech(input, voice, audioConfig);

            ByteString audioContents = response.getAudioContent();

            return audioContents.toByteArray();
        }
    }
}