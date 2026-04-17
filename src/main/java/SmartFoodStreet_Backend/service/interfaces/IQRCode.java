package SmartFoodStreet_Backend.service.interfaces;

import SmartFoodStreet_Backend.dto.qrcode.QRCodeCreateRequest;
import SmartFoodStreet_Backend.dto.qrcode.QRCodeResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

public interface IQRCode {

   String handleScan(String code, HttpServletRequest request, String sessionId);

   QRCodeResponse create(QRCodeCreateRequest request);

   QRCodeResponse update(Long id, QRCodeCreateRequest request);

   void delete(Long id);

   QRCodeResponse toggleActive(Long id);

   QRCodeResponse getById(Long id);

   QRCodeResponse getByStall(Long stallId);

   List<QRCodeResponse> getAll();
}
