package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.common.response.CloudinaryResponse;
import SmartFoodStreet_Backend.entity.StallTranslation;
import SmartFoodStreet_Backend.enums.AudioStatus;
import SmartFoodStreet_Backend.repository.StallTranslationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class AudioProcessorService {

   private final StallTranslationRepository repository;
   private final TtsService ttsService;
   private final CloudinaryService cloudinaryService;
   private final TranslationService translationService;

   // QUAN TRỌNG: Nhận thẳng Object StallTranslation thay vì Long translationId
   @Async
   public void processAudioAsync(StallTranslation stallTranslation, Long stallId, String targetLanguage) {

      try {
         log.info("Bắt đầu xử lý Audio ngầm cho stall: {}, language: {}", stallId, targetLanguage);

         String scriptToRead = stallTranslation.getTtsScript();

         // 1. DỊCH (Nếu kịch bản đang trống)
         if (scriptToRead == null || scriptToRead.trim().isEmpty()) {
            StallTranslation baseTranslation = repository.findByStallIdAndLanguageCode(stallId, "vi")
                  .orElseThrow(() -> new RuntimeException("Không tìm thấy bản gốc tiếng Việt để dịch"));

            scriptToRead = translationService.translateText(baseTranslation.getTtsScript(), "vi", targetLanguage);
            stallTranslation.setTtsScript(scriptToRead); // Lưu lại bản dịch
         }

         // 2. LẤY GIỌNG ĐỌC
         String voiceConfig = getVoiceConfigForLanguage(targetLanguage);

         // 3. GEN AUDIO
         byte[] audio = ttsService.generate(scriptToRead, targetLanguage, voiceConfig);

         if (audio == null || audio.length == 0) {
            throw new RuntimeException("Không thể tạo file âm thanh (TTS Service có thể chưa được cấu hình)");
         }

         // 4. UPLOAD LÊN CLOUD (Ghi đè)
         String publicId = "stall_" + stallId + "_" + targetLanguage;
         CloudinaryResponse upload = cloudinaryService.uploadAudioWithOverwrite(audio, publicId);

         // 5. CẬP NHẬT THÀNH CÔNG
         String hash = DigestUtils.md5DigestAsHex(audio);
         stallTranslation.setAudioUrl(upload.getUrl());
         stallTranslation.setFileSize(upload.getBytes());
         stallTranslation.setAudioHash(hash);
         stallTranslation.setAudioStatus(AudioStatus.COMPLETED);

         log.info("Xử lý thành công ngôn ngữ: {}", targetLanguage);

      } catch (Exception e) {
         log.error("Lỗi khi xử lý ngầm Audio cho ngôn ngữ {}: {}", targetLanguage, e.getMessage(), e);
         stallTranslation.setAudioStatus(AudioStatus.ERROR);
      }

      // Lưu trạng thái cuối cùng vào Database (Dù thành công hay lỗi)
      repository.save(stallTranslation);
   }

   private String getVoiceConfigForLanguage(String languageCode) {
      return switch (languageCode.toLowerCase()) {
         case "vi" -> "vi-VN-Standard-A";
         case "en-US" -> "en-US-Neural2-F";
         case "ko" -> "ko-KR-Standard-A";
         case "ja" -> "ja-JP-Standard-A";
         default -> "en-US-Standard-A";
      };
   }
}