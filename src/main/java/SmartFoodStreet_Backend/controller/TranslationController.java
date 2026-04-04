package SmartFoodStreet_Backend.controller;

import SmartFoodStreet_Backend.common.response.ApiResponse;
import SmartFoodStreet_Backend.dto.translation.TranslationRequest;
import SmartFoodStreet_Backend.service.TranslationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/translations")
@RequiredArgsConstructor
public class TranslationController {

    private final TranslationService translationService;

    @PostMapping
    public ApiResponse<String> testTranslate(@Valid @RequestBody TranslationRequest request) {

        String translatedText = translationService.translateText(
                request.getText(),
                request.getSourceLang(),
                request.getTargetLang()
        );

        return ApiResponse.<String>builder()
                .result(translatedText)
                .message("SUCCESSFULLY")
                .build();
    }
}