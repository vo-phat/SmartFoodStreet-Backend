package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.common.response.CloudinaryResponse;
import SmartFoodStreet_Backend.entity.StallTranslation;
import SmartFoodStreet_Backend.enums.AudioStatus;
import SmartFoodStreet_Backend.enums.LanguageCode;
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

    @Async("audioExecutor")
    public void processAudioAsync(Long translationId, Long stallId, String targetLanguage) {
        try {

            log.info("STEP 1 - start async");

            // LOAD LẠI ENTITY TỪ DB
            StallTranslation translation =
                    repository.findById(translationId)
                            .orElseThrow();

            log.info("STEP 2 - translation found");

//            translation.setAudioStatus(AudioStatus.PROCESSING);
//            repository.save(translation);

            log.info("STEP 3 - set PROCESSING");

            String scriptToRead = translation.getTtsScript();

            if (scriptToRead == null || scriptToRead.trim().isEmpty()) {

                StallTranslation baseTranslation =
                        repository.findByStallIdAndLanguageCode(
                                        stallId,
                                        "vi-VN"
                                )
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Không tìm thấy bản gốc tiếng Việt"
                                        ));

                scriptToRead = baseTranslation.getTtsScript();

                if (!targetLanguage.equalsIgnoreCase("vi-VN")) {

                    scriptToRead = translationService.translateText(
                            scriptToRead,
                            "vi-VN",
                            targetLanguage
                    );
                }
            }

            translation.setTtsScript(scriptToRead);

            repository.saveAndFlush(translation);

            log.info("STEP 4 - script ready");

            LanguageCode language =
                    LanguageCode.fromCode(targetLanguage);

            String voiceConfig = language.getVoice();

            byte[] audio =
                    ttsService.generate(
                            scriptToRead,
                            targetLanguage,
                            voiceConfig
                    );

            log.info("STEP 5 - tts generated");

            if (audio == null || audio.length == 0) {
                throw new RuntimeException("Audio empty");
            }

            String publicId =
                    "stall_" + stallId + "_" +
                            targetLanguage.replace("-", "_");

            CloudinaryResponse upload =
                    cloudinaryService.uploadAudioWithOverwrite(
                            audio,
                            publicId
                    );

            log.info("STEP 6 - cloudinary uploaded");

            String hash =
                    DigestUtils.md5DigestAsHex(audio);

            translation.setAudioUrl(upload.getUrl());
            translation.setFileSize(upload.getBytes());
            translation.setAudioHash(hash);
            translation.setAudioStatus(AudioStatus.COMPLETED);

            repository.saveAndFlush(translation);

            log.info("STEP 7 - COMPLETED");

        } catch (Exception e) {

            log.error("AUDIO FAILED", e);

            try {

                StallTranslation translation =
                        repository.findById(translationId)
                                .orElse(null);

                if (translation != null) {

                    translation.setAudioStatus(AudioStatus.ERROR);

                    repository.saveAndFlush(translation);
                }

            } catch (Exception ex) {

                log.error("FAILED SAVE ERROR STATUS", ex);
            }
        }
    }
}