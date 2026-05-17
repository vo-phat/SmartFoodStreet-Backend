package backend.service.interfaces;

import backend.dto.qrcode.QRCodeCreateRequest;
import backend.dto.qrcode.QRCodeResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

public interface IQRCode {

   String handleScan(String code, HttpServletRequest request, String sessionId);

   QRCodeResponse create(QRCodeCreateRequest request);

   QRCodeResponse update(Long id, QRCodeCreateRequest request);

   void delete(Long id);

   QRCodeResponse toggleActive(Long id);
   QRCodeResponse regenerateCode(Long id);
   QRCodeResponse getById(Long id);

   QRCodeResponse getByStall(Long stallId);

   List<QRCodeResponse> getAll();
}