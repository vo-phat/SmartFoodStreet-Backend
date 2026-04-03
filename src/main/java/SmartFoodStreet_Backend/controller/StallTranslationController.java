package SmartFoodStreet_Backend.controller;

import SmartFoodStreet_Backend.common.response.ApiResponse;
import SmartFoodStreet_Backend.dto.stall.request.StallTranslationRequest;
import SmartFoodStreet_Backend.dto.stall.response.StallTranslationResponse;
import SmartFoodStreet_Backend.service.interfaces.IStallTranslation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stall-translations")
@RequiredArgsConstructor
public class StallTranslationController {

    private final IStallTranslation translationService;

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody StallTranslationRequest stallTranslationRequest) {
        translationService.create(stallTranslationRequest);
        return ApiResponse.<Void>builder()
                .message("Successfully")
                .build();
    }

    @GetMapping
    public ApiResponse<StallTranslationResponse> get(@RequestParam Long stallId, @RequestParam String languageCode) {

        return ApiResponse.<StallTranslationResponse>builder()
                .result(translationService.get(stallId, languageCode))
                .build();
    }
}