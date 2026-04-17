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

   @Override
   @Transactional
   public String handleScan(String code, HttpServletRequest request, String sessionId) {

      // 1. Kiểm tra mã QR hợp lệ
      if (code == null || code.isBlank()) {
         throw new RuntimeException("Mã QR không hợp lệ");
      }

      // 2. Tìm kiếm QR trong database
      QRCode qr = qrCodeRepository.findByCodeWithStall(code)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy mã QR"));

      validateQrCode(qr);

      // 4. Lấy thông tin Gian hàng (Stall)
      Stall stall = qr.getStall();
      
      String ip = getClientIp(request);

      // 4. Chống spam (quan trọng)
      boolean isSpam = isDuplicateScan(qr.getCode(), ip);
      if (isSpam) {
         throw new AppException(ErrorCode.TOO_MANY_REQUESTS);
      }

      // 5. Ghi nhận sự kiện quét mã QR
      VisitEvent event = buildEvent(qr, request, ip, sessionId);
      visitEventAsyncService.logEventAsync(event);

      // 6. Cập nhật lượt quét
      qrCodeRepository.incrementScanCount(qr.getId());

      if (stall == null) {
          return "http://localhost:5173/home";
      }

      if (!Boolean.TRUE.equals(stall.getIsActive())) {
         throw new RuntimeException("Gian hàng hiện không hoạt động");
      }

      return "http://localhost:5173/stall/" + stall.getId();
   }

   private boolean isDuplicateScan(String code, String ip) {
      LocalDateTime thirtySecondsAgo = LocalDateTime.now().minusSeconds(30);
      return visitEventRepository.existsByQrCodeAndIpAddressAndEventTimeAfter(
            code,
            ip,
            thirtySecondsAgo);
   }

   private VisitEvent buildEvent(QRCode qr, HttpServletRequest request, String ip, String sessionId) {
      LocalDateTime now = LocalDateTime.now();
      return VisitEvent.builder()
            .stallId(qr.getStall() != null ? qr.getStall().getId() : null)
            .qrCode(qr.getCode())
            .eventType(qr.getStall() == null ? VisitEvent.EventType.WEBSITE_VISIT : VisitEvent.EventType.QR_SCAN)
            .eventTime(now)
            .ipAddress(ip)
            .userAgent(request.getHeader("User-Agent"))
            .sessionId(sessionId != null && !sessionId.isEmpty() ? Long.valueOf(sessionId) : null)
            .hour(now.getHour())
            .day(now.getDayOfMonth())
            .month(now.getMonthValue())
            .year(now.getYear())
            .build();
   }

   private String getClientIp(HttpServletRequest request) {
      String remoteAddr = "";
      if (request != null) {
         remoteAddr = request.getHeader("X-FORWARDED-FOR");
         if (remoteAddr == null || "".equals(remoteAddr)) {
            remoteAddr = request.getRemoteAddr();
         }
      }
      return remoteAddr;
   }

   private boolean validateQrCode(QRCode qr) {
      if (!Boolean.TRUE.equals(qr.getIsActive())) {
         throw new RuntimeException("Mã QR này hiện đang bị khóa");
      }
      return true;
   }
}
