package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.common.exception.AppException;
import SmartFoodStreet_Backend.common.exception.ErrorCode;
import SmartFoodStreet_Backend.entity.QRCode;
import SmartFoodStreet_Backend.entity.Stall;
import SmartFoodStreet_Backend.entity.VisitEvent;
import SmartFoodStreet_Backend.repository.QRCodeRepository;
import SmartFoodStreet_Backend.repository.VisitEventRepository;
import SmartFoodStreet_Backend.service.interfaces.IQRCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import SmartFoodStreet_Backend.repository.StallRepository;
import SmartFoodStreet_Backend.mapper.QRCodeMapper;
import SmartFoodStreet_Backend.dto.qrcode.QRCodeCreateRequest;
import SmartFoodStreet_Backend.dto.qrcode.QRCodeResponse;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QRCodeService implements IQRCode {

   private final QRCodeRepository qrCodeRepository;
   private final VisitEventRepository visitEventRepository;
   private final VisitEventAsyncService visitEventAsyncService;
   private final StallRepository stallRepository;
   private final QRCodeMapper qrCodeMapper;

   @Override
   @Transactional
   public QRCodeResponse create(QRCodeCreateRequest request) {
      String code = request.getCode();
      if (code == null || code.isBlank()) {
         code = java.util.UUID.randomUUID().toString();
      }

      if (qrCodeRepository.findByCode(code).isPresent()) {
         throw new RuntimeException("Mã QR code đã tồn tại");
      }

      Stall stall = null;
      if (request.getStallId() != null) {
         if (qrCodeRepository.findByStallId(request.getStallId()).isPresent()) {
            throw new RuntimeException("Gian hàng này đã có mã QR code. Mỗi gian hàng chỉ được có 1 mã duy nhất.");
         }
         stall = stallRepository.findById(request.getStallId())
               .orElseThrow(() -> new RuntimeException("Không tìm thấy gian hàng"));
      }

      QRCode qrCode = QRCode.builder()
            .name(request.getName())
            .code(code)
            .stall(stall)
            .isActive(request.getIsActive() != null ? request.getIsActive() : true)
            .scanCount(0)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

      return qrCodeMapper.toResponse(qrCodeRepository.save(qrCode));
   }

   @Override
   @Transactional
   public QRCodeResponse update(Long id, QRCodeCreateRequest request) {
      QRCode qrCode = qrCodeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy mã QR"));

      if (request.getCode() != null && !request.getCode().equals(qrCode.getCode())) {
         if (qrCodeRepository.findByCode(request.getCode()).isPresent()) {
            throw new RuntimeException("Mã QR code này đã được sử dụng");
         }
         qrCode.setCode(request.getCode());
      }

      if (request.getStallId() != null && !request.getStallId().equals(qrCode.getStall().getId())) {
         if (qrCodeRepository.findByStallId(request.getStallId()).isPresent()) {
            throw new RuntimeException("Gian hàng mục tiêu đã có mã QR code");
         }
         Stall stall = stallRepository.findById(request.getStallId())
               .orElseThrow(() -> new RuntimeException("Không tìm thấy gian hàng"));
         qrCode.setStall(stall);
      }

      if (request.getName() != null)
         qrCode.setName(request.getName());
      if (request.getIsActive() != null)
         qrCode.setIsActive(request.getIsActive());

      qrCode.setUpdatedAt(LocalDateTime.now());

      return qrCodeMapper.toResponse(qrCodeRepository.save(qrCode));
   }

   @Override
   @Transactional
   public void delete(Long id) {
      if (!qrCodeRepository.existsById(id)) {
         throw new RuntimeException("Không tìm thấy mã QR");
      }
      qrCodeRepository.deleteById(id);
   }

   @Override
   @Transactional
   public QRCodeResponse toggleActive(Long id) {
      QRCode qrCode = qrCodeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy mã QR"));

      qrCode.setIsActive(!Boolean.TRUE.equals(qrCode.getIsActive()));
      qrCode.setUpdatedAt(LocalDateTime.now());

      return qrCodeMapper.toResponse(qrCodeRepository.save(qrCode));
   }

   @Override
   @Transactional
   public QRCodeResponse regenerateCode(Long id) {
      QRCode qrCode = qrCodeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy mã QR"));

      qrCode.setCode(java.util.UUID.randomUUID().toString());
      qrCode.setUpdatedAt(LocalDateTime.now());

      return qrCodeMapper.toResponse(qrCodeRepository.save(qrCode));
   }
   @Override
   public QRCodeResponse getById(Long id) {
      return qrCodeRepository.findById(id)
            .map(qrCodeMapper::toResponse)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy mã QR"));
   }

   @Override
   public QRCodeResponse getByStall(Long stallId) {
      return qrCodeRepository.findByStallId(stallId)
            .map(qrCodeMapper::toResponse)
            .orElseThrow(() -> new RuntimeException("Gian hàng này chưa có mã QR code"));
   }

   @Override
   public List<QRCodeResponse> getAll() {
      return qrCodeMapper.toResponseList(qrCodeRepository.findAll());
   }

    /**
     * Bước 1: Chỉ kiểm tra và trả về đường dẫn điều hướng kèm ID xác nhận
     */
    @Override
    @Transactional(readOnly = true)
    public String handleScan(String code, HttpServletRequest request, String sessionId) {
        if (code == null || code.isBlank()) throw new RuntimeException("Mã QR không hợp lệ");

        QRCode qr = qrCodeRepository.findByCodeWithStall(code)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã QR"));

        if (!Boolean.TRUE.equals(qr.getIsActive())) throw new RuntimeException("Mã QR này hiện đang bị khóa");

        Stall stall = qr.getStall();
        String path = (stall == null) ? "/home" : "/home/stall/" + stall.getId();

        // Trả về Path kèm qr_confirm_id để Frontend gọi lại confirm
        return path + (path.contains("?") ? "&" : "?") + "qr_confirm_id=" + qr.getId();
    }

    /**
     * Bước 2: Khi Frontend load trang thành công, gọi hàm này để chính thức đếm lượt
     */
    @Transactional
    public void confirmAndCount(Long qrId, HttpServletRequest request, String sessionId) {
        QRCode qr = qrCodeRepository.findById(qrId)
                .orElseThrow(() -> new RuntimeException("Xác nhận QR thất bại: Không tìm thấy ID"));

//      nếu muốn đếm cả lượt vào stall thì bỏ if
        if (qr.getStall() == null) {
            String ip = getClientIp(request);

            // Chống spam trong 10 giây
            if (isDuplicateScan(qr.getCode(), ip)) return;

            // Lưu log sự kiện Website Visit
            VisitEvent event = buildEvent(qr, request, ip, sessionId);
            visitEventRepository.save(event);

            // Tăng lượt quét cho mã Gateway
            qrCodeRepository.incrementScanCount(qr.getId());
        } else {
            // Nếu là mã Stall, chúng ta có thể bỏ qua không đếm,
            // hoặc chỉ ghi log mà không tăng scanCount tùy bạn.
            System.out.println("Bỏ qua đếm lượt cho mã QR gian hàng ID: " + qrId);
        }
    }

    private boolean isDuplicateScan(String code, String ip) {
        LocalDateTime delay = LocalDateTime.now().minusSeconds(10); // Giảm xuống 10s để test dễ hơn
        return visitEventRepository.existsByQrCodeAndIpAddressAndEventTimeAfter(code, ip, delay);
    }

    private VisitEvent buildEvent(QRCode qr, HttpServletRequest request, String ip, String sessionId) {
        Long sid = null;
        try {
            if (sessionId != null && !sessionId.isBlank()) sid = Long.valueOf(sessionId);
        } catch (Exception e) { /* Ignore */ }

        LocalDateTime now = LocalDateTime.now();
        return VisitEvent.builder()
                .stallId(qr.getStall() != null ? qr.getStall().getId() : null)
                .qrCode(qr.getCode())
                .eventType(qr.getStall() == null ? VisitEvent.EventType.WEBSITE_VISIT : VisitEvent.EventType.QR_SCAN)
                .eventTime(now)
                .ipAddress(ip)
                .userAgent(request.getHeader("User-Agent"))
                .sessionId(sid)
                .hour(now.getHour()).day(now.getDayOfMonth()).month(now.getMonthValue()).year(now.getYear())
                .build();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-FORWARDED-FOR");
        return (ip == null || ip.isEmpty()) ? request.getRemoteAddr() : ip.split(",")[0];
    }

   private boolean validateQrCode(QRCode qr) {
      if (!Boolean.TRUE.equals(qr.getIsActive())) {
         throw new RuntimeException("Mã QR này hiện đang bị khóa");
      }
      return true;
   }
}