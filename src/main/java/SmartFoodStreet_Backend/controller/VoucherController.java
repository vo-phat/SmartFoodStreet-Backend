package SmartFoodStreet_Backend.controller;

import SmartFoodStreet_Backend.common.response.ApiResponse;
import SmartFoodStreet_Backend.dto.voucher.request.GenerateVoucherRequest;
import SmartFoodStreet_Backend.dto.voucher.response.VoucherResponse;
import SmartFoodStreet_Backend.service.interfaces.IVoucher;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final IVoucher voucherService;

    // Generate voucher (mobile gọi hoặc geofence gọi nội bộ)
    @PostMapping("/generate")
    public ApiResponse<VoucherResponse> generate(
            @RequestBody @Valid GenerateVoucherRequest request) {

        return ApiResponse.<VoucherResponse>builder()
                .result(voucherService.generate(request))
                .build();
    }

    // Vendor redeem
    @PostMapping("/{code}/redeem")
    public ApiResponse<VoucherResponse> redeem(
            @PathVariable String code) {

        return ApiResponse.<VoucherResponse>builder()
                .result(voucherService.redeem(code))
                .build();
    }
}