package SmartFoodStreet_Backend.controller;

import SmartFoodStreet_Backend.common.response.ApiResponse;
import SmartFoodStreet_Backend.dto.stall.request.StallTranslationRequest;
import SmartFoodStreet_Backend.dto.stall.response.StallAudioResponse;
import SmartFoodStreet_Backend.dto.stall.response.StallTranslationResponse;
import SmartFoodStreet_Backend.service.interfaces.IStallTranslation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stall-translations")
@RequiredArgsConstructor
public class StallTranslationController {
    private final IStallTranslation service;

    /**
     * Tạo translation
     */
    @PostMapping
    public ApiResponse<StallTranslationResponse> create(@Valid @RequestBody StallTranslationRequest request) {

        return ApiResponse.<StallTranslationResponse>builder()
                .result(service.create(request))
                .build();
    }

    /**
     * Update translation
     */
    @PutMapping("/{id}")
    public ApiResponse<StallTranslationResponse> update(@PathVariable Long id, @Valid @RequestBody StallTranslationRequest request) {
        return ApiResponse.<StallTranslationResponse>builder()
                .result(service.update(id, request))
                .build();
    }

    /**
     * Lấy theo ID
     */
    @GetMapping("/{id}")
    public ApiResponse<StallTranslationResponse> getById(@PathVariable Long id) {
        return ApiResponse.<StallTranslationResponse>builder()
                .result(service.getById(id))
                .build();
    }

    /**
     * Lấy tất cả translation của 1 stall
     */
    @GetMapping("/stall/{stallId}")
    public ApiResponse<List<StallTranslationResponse>> getByStall(@PathVariable Long stallId) {
        return ApiResponse.<List<StallTranslationResponse>>builder()
                .result(service.getByStall(stallId))
                .build();
    }

    /**
     * Xóa
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);

        return ApiResponse.<Void>builder()
                .message("Delete Stall Translation By ID Successfully")
                .build();
    }

    /**
     * Lấy audio (Zero latency flow)
     * clientHash:
     * - null → chưa có audio
     * - có → dùng để check cache
     */
    @GetMapping("/audio")
    public ApiResponse<StallAudioResponse> getAudio(@RequestParam Long stallId, @RequestParam String language, @RequestParam(required = false) String clientHash) {
        return ApiResponse.<StallAudioResponse>builder()
                .message("Get Audio Successfully")
                .result(service.getAudio(stallId, language, clientHash))
                .build();
    }

    /**
     * Update translation
     */
    @PutMapping("/{id}")
    public ApiResponse<StallTranslationResponse> update(@PathVariable Long id,
            @Valid @RequestBody StallTranslationRequest request) {
        return ApiResponse.<StallTranslationResponse>builder()
                .result(service.update(id, request))
                .build();
    }

    /**
     * Lấy theo ID
     */
    @GetMapping("/{id}")
    public ApiResponse<StallTranslationResponse> getById(@PathVariable Long id) {
        return ApiResponse.<StallTranslationResponse>builder()
                .result(service.getById(id))
                .build();
    }

    /**
     * Lấy tất cả translation của 1 stall
     */
    @GetMapping("/stall/{stallId}")
    public ApiResponse<List<StallTranslationResponse>> getByStall(@PathVariable Long stallId) {
        return ApiResponse.<List<StallTranslationResponse>>builder()
                .result(service.getByStall(stallId))
                .build();
    }

    /**
     * Xóa
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);

        return ApiResponse.<Void>builder()
                .message("Delete Stall Translation By ID Successfully")
                .build();
    }

    /**
     * Lấy audio (Zero latency flow)
     * clientHash:
     * - null → chưa có audio
     * - có → dùng để check cache
     */
    @GetMapping("/audio")
    public ApiResponse<StallAudioResponse> getAudio(@RequestParam Long stallId, @RequestParam String language,
            @RequestParam(required = false) String clientHash) {
        return ApiResponse.<StallAudioResponse>builder()
                .message("Get Audio Successfully")
                .result(service.getAudio(stallId, language, clientHash))
                .build();
    }
}
