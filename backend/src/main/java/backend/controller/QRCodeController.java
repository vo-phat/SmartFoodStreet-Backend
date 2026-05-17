package backend.controller;

import backend.common.response.ApiResponse;
import backend.dto.qrcode.QRCodeCreateRequest;
import backend.dto.qrcode.QRCodeResponse;
import backend.service.QRCodeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/qr")
@RequiredArgsConstructor
@CrossOrigin("*")
public class QRCodeController {

    private final QRCodeService qrCodeService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

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

    @PatchMapping("/{id}/regenerate")
    public ApiResponse<QRCodeResponse> regenerateCode(@PathVariable Long id) {
        return ApiResponse.<QRCodeResponse>builder()
                .result(qrCodeService.regenerateCode(id))
                .build();
    }

    @GetMapping("/stall/{stallId}")
    public ApiResponse<QRCodeResponse> getByStall(@PathVariable Long stallId) {
        return ApiResponse.<QRCodeResponse>builder()
                .result(qrCodeService.getByStall(stallId))
                .build();
    }

    @GetMapping("/gateway")
    public ApiResponse<QRCodeResponse> getGateway() {
        return ApiResponse.<QRCodeResponse>builder()
                .result(qrCodeService.getAll().stream()
                        .filter(q -> q.getStallId() == null)
                        .findFirst()
                        .orElse(null))
                .build();
    }

    @GetMapping
    public ApiResponse<List<QRCodeResponse>> getAll() {
        return ApiResponse.<List<QRCodeResponse>>builder()
                .result(qrCodeService.getAll())
                .build();
    }

    /**
     * API quét mã QR.
     * Trả về HTML + JS để điều hướng phía Client nhằm TRÁNH LỖI 404 CACHE.
     */
    @GetMapping(value = "/scan/{code}", produces = "text/html; charset=UTF-8")
    @ResponseBody
    public String scanQRCode(
            @PathVariable String code,
            @RequestParam(required = false) String sessionId,
            HttpServletRequest request) {

        try {
            String targetPath = qrCodeService.handleScan(code, request, sessionId);

            // Tạo link tuyệt đối + Cache busting (t=timestamp)
            String separator = targetPath.contains("?") ? "&" : "?";
            String finalUrl = (frontendUrl.endsWith("/") ? frontendUrl : frontendUrl + "/")
                    + (targetPath.startsWith("/") ? targetPath.substring(1) : targetPath)
                    + separator + "t=" + System.currentTimeMillis();

            // Trả về mã HTML để ép trình duyệt chuyển hướng bằng JavaScript
            return "<html>" +
                    "<head><title>Redirecting...</title></head>" +
                    "<body style='background:#0f172a; color:white; font-family:sans-serif; display:flex; align-items:center; justify-content:center; height:100vh;'>" +
                    "  <div style='text-align:center;'>" +
                    "    <div style='border:4px solid #f97316; border-top:4px solid transparent; border-radius:50%; width:40px; height:40px; animate:spin 1s linear infinite; margin:0 auto 20px;'></div>" +
                    "    <p style='font-weight:bold; letter-spacing:0.1em; font-size:12px;'>ĐANG CHUYỂN HƯỚNG ĐẾN SMART FOOD STREET...</p>" +
                    "  </div>" +
                    "  <script type='text/javascript'>" +
                    "    window.location.replace('" + finalUrl + "');" +
                    "  </script>" +
                    "  <style>@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }</style>" +
                    "</body>" +
                    "</html>";

        } catch (Exception e) {
            String errorUrl = frontendUrl + "/error?message=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            return "<html><body><script>window.location.replace('" + errorUrl + "');</script></body></html>";
        }
    }

    /**
     * API xác nhận thành công từ Frontend
     */
    @PostMapping("/confirm-success/{qrId}")
    public ApiResponse<Void> confirmSuccess(
            @PathVariable Long qrId,
            @RequestParam(required = false) String sessionId,
            HttpServletRequest request) {

        qrCodeService.confirmAndCount(qrId, request, sessionId);
        return ApiResponse.<Void>builder().message("Xác nhận thành công").build();
    }
}