package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.entity.QRCode;
import SmartFoodStreet_Backend.entity.Stall;
import SmartFoodStreet_Backend.repository.StallRepository;
import SmartFoodStreet_Backend.service.interfaces.IQRCode;
import jakarta.servlet.http.HttpServletRequest;

public class QRCodeService implements IQRCode {
   private StallRepository stallRepository;

   public String handleScan(String code, HttpServletRequest request) {

      // 1. Validate input
      if (code == null || code.isBlank()) {
         throw new RuntimeException("Invalid QR code");
      }

      // 2. Lookup QR
      QRCode qr = qrCodeRepository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("QR not found"));

      if (!Boolean.TRUE.equals(qr.getIsActive())) {
         throw new RuntimeException("QR is disabled");
      }

      // 3. Lấy Stall
      Stall stall = stallRepository.findById(qr.getStallId())
            .orElseThrow(() -> new RuntimeException("Stall not found"));

      if (!Boolean.TRUE.equals(stall.getIsActive())) {
         throw new RuntimeException("Stall inactive");
      }

      // 4. Anti-spam (🔥 cực quan trọng)
      boolean isSpam = isDuplicateScan(code, request);
      if (!isSpam) {

         // 5. Log event
         visitEventService.logQrScan(stall.getId(), code, request);

         // 6. Update counter (có thể optimize sau)
         qr.setScanCount(qr.getScanCount() + 1);
         qrCodeRepository.save(qr);
      }

      // 7. Redirect URL (frontend)
      return "/stall/" + stall.getId();
   }

}
