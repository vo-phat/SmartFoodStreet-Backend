package SmartFoodStreet_Backend.controller;

import SmartFoodStreet_Backend.common.response.ApiResponse;
import SmartFoodStreet_Backend.dto.gps.request.GpsCheckRequest;
import SmartFoodStreet_Backend.dto.gps.response.GpsCheckResponse;
import SmartFoodStreet_Backend.service.interfaces.IGps;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gps")
@RequiredArgsConstructor
public class GpsController {

    private final IGps gpsService;

    @PostMapping("/check")
    public ApiResponse<GpsCheckResponse> check(
            @RequestBody @Valid GpsCheckRequest request) {

        return ApiResponse.<GpsCheckResponse>builder()
                .result(gpsService.check(request))
                .build();
    }
}
