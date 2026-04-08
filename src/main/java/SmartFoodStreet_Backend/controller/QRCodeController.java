package SmartFoodStreet_Backend.controller;

import SmartFoodStreet_Backend.common.response.ApiResponse;
import SmartFoodStreet_Backend.dto.qrcode.QRCodeCreateRequest;
import SmartFoodStreet_Backend.dto.qrcode.QRCodeResponse;
import SmartFoodStreet_Backend.service.QRCodeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/qr")
@RequiredArgsConstructor
@CrossOrigin("*")
public class QRCodeController {

    private final QRCodeService qrCodeService;

    @PostMapping
    public ApiResponse<QRCodeResponse> create(@Valid @RequestBody QRCodeCreateRequest request) {
        return ApiResponse.<QRCodeResponse>builder()
                .result(qrCodeService.create(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<QRCodeResponse> update(@PathVariable Long id, @Valid @RequestBody QRCodeCreateRequest request) {
        return ApiResponse.<QRCodeResponse>builder()
                .result(qrCodeService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        qrCodeService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Successfully deleted QR code")
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<QRCodeResponse> getById(@PathVariable Long id) {
        return ApiResponse.<QRCodeResponse>builder()
                .result(qrCodeService.getById(id))
                .build();
    }

    @PatchMapping("/{id}/toggle")
    public ApiResponse<QRCodeResponse> toggleActive(@PathVariable Long id) {
        return ApiResponse.<QRCodeResponse>builder()
                .result(qrCodeService.toggleActive(id))
                .build();
    }

    @GetMapping("/stall/{stallId}")
    public ApiResponse<QRCodeResponse> getByStall(@PathVariable Long stallId) {
        return ApiResponse.<QRCodeResponse>builder()
                .result(qrCodeService.getByStall(stallId))
                .build();
    }

    @GetMapping
    public ApiResponse<List<QRCodeResponse>> getAll() {
        return ApiResponse.<List<QRCodeResponse>>builder()
                .result(qrCodeService.getAll())
                .build();
    }

    @GetMapping("/scan/{code}")
    public void scanQRCode(
            @PathVariable String code,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        try {
            String targetUrl = qrCodeService.handleScan(code, request);

            response.sendRedirect(targetUrl);

        } catch (Exception e) {

            response.sendRedirect("http://localhost:5173/error");
        }
    }
}
