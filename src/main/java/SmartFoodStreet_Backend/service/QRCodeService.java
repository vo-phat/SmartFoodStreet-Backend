package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.common.exception.AppException;
import SmartFoodStreet_Backend.common.exception.ErrorCode;
import SmartFoodStreet_Backend.dto.qrcode.QRCodeCreateRequest;
import SmartFoodStreet_Backend.dto.qrcode.QRCodeResponse;
import SmartFoodStreet_Backend.entity.QRCode;
import SmartFoodStreet_Backend.entity.Stall;
import SmartFoodStreet_Backend.entity.VisitEvent;
import SmartFoodStreet_Backend.enums.VisitEventType;
import SmartFoodStreet_Backend.mapper.QRCodeMapper;
import SmartFoodStreet_Backend.repository.QRCodeRepository;
import SmartFoodStreet_Backend.repository.StallRepository;
import SmartFoodStreet_Backend.repository.VisitEventRepository;
import SmartFoodStreet_Backend.service.interfaces.IQRCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QRCodeService implements IQRCode {

    private final AnalyticsService analyticsService;

    private final QRCodeRepository qrCodeRepository;

    private final VisitEventRepository visitEventRepository;

    private final VisitEventAsyncService visitEventAsyncService;

    private final StallRepository stallRepository;

    private final QRCodeMapper qrCodeMapper;

    private final AnalyticsRealtimeService analyticsRealtimeService;

    @Override
    @Transactional
    public QRCodeResponse create(QRCodeCreateRequest request) {

        String code = request.getCode();

        if (code == null || code.isBlank()) {
            code = java.util.UUID.randomUUID().toString();
        }

        if (qrCodeRepository.findByCode(code).isPresent()) {
            throw new RuntimeException(
                    "Mã QR code đã tồn tại"
            );
        }

        Stall stall = null;

        if (request.getStallId() != null) {

            if (
                    qrCodeRepository
                            .findByStallId(
                                    request.getStallId()
                            )
                            .isPresent()
            ) {

                throw new RuntimeException(
                        "Gian hàng này đã có mã QR code"
                );
            }

            stall = stallRepository
                    .findById(request.getStallId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Không tìm thấy gian hàng"
                            )
                    );
        }

        QRCode qrCode = QRCode.builder()
                .name(request.getName())
                .code(code)
                .stall(stall)
                .isActive(
                        request.getIsActive() != null
                                ? request.getIsActive()
                                : true
                )
                .scanCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return qrCodeMapper.toResponse(
                qrCodeRepository.save(qrCode)
        );
    }

    @Override
    @Transactional
    public QRCodeResponse update(
            Long id,
            QRCodeCreateRequest request
    ) {

        QRCode qrCode = qrCodeRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy mã QR"
                        )
                );

        if (
                request.getCode() != null &&
                        !request.getCode().equals(qrCode.getCode())
        ) {

            if (
                    qrCodeRepository
                            .findByCode(request.getCode())
                            .isPresent()
            ) {

                throw new RuntimeException(
                        "Mã QR code đã tồn tại"
                );
            }

            qrCode.setCode(request.getCode());
        }

        if (
                request.getStallId() != null &&
                        (
                                qrCode.getStall() == null ||
                                        !request.getStallId().equals(
                                                qrCode.getStall().getId()
                                        )
                        )
        ) {

            if (
                    qrCodeRepository
                            .findByStallId(
                                    request.getStallId()
                            )
                            .isPresent()
            ) {

                throw new RuntimeException(
                        "Gian hàng đã có QR code"
                );
            }

            Stall stall = stallRepository
                    .findById(request.getStallId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Không tìm thấy gian hàng"
                            )
                    );

            qrCode.setStall(stall);
        }

        if (request.getName() != null) {
            qrCode.setName(request.getName());
        }

        if (request.getIsActive() != null) {
            qrCode.setIsActive(request.getIsActive());
        }

        qrCode.setUpdatedAt(LocalDateTime.now());

        return qrCodeMapper.toResponse(
                qrCodeRepository.save(qrCode)
        );
    }

    @Override
    @Transactional
    public void delete(Long id) {

        if (!qrCodeRepository.existsById(id)) {

            throw new RuntimeException(
                    "Không tìm thấy mã QR"
            );
        }

        qrCodeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public QRCodeResponse toggleActive(Long id) {

        QRCode qrCode = qrCodeRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy mã QR"
                        )
                );

        qrCode.setIsActive(
                !Boolean.TRUE.equals(
                        qrCode.getIsActive()
                )
        );

        qrCode.setUpdatedAt(LocalDateTime.now());

        return qrCodeMapper.toResponse(
                qrCodeRepository.save(qrCode)
        );
    }

    @Override
    @Transactional
    public QRCodeResponse regenerateCode(Long id) {

        QRCode qrCode = qrCodeRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy mã QR"
                        )
                );

        qrCode.setCode(
                java.util.UUID.randomUUID().toString()
        );

        qrCode.setUpdatedAt(LocalDateTime.now());

        return qrCodeMapper.toResponse(
                qrCodeRepository.save(qrCode)
        );
    }

    @Override
    public QRCodeResponse getById(Long id) {

        return qrCodeRepository
                .findById(id)
                .map(qrCodeMapper::toResponse)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy mã QR"
                        )
                );
    }

    @Override
    public QRCodeResponse getByStall(Long stallId) {

        return qrCodeRepository
                .findByStallId(stallId)
                .map(qrCodeMapper::toResponse)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Gian hàng chưa có QR code"
                        )
                );
    }

    @Override
    public List<QRCodeResponse> getAll() {

        return qrCodeMapper.toResponseList(
                qrCodeRepository.findAll()
        );
    }

    @Override
    @Transactional
    public String handleScan(
            String code,
            HttpServletRequest request,
            String deviceId
    ) {

        // =====================================================
        // validate
        // =====================================================

        if (code == null || code.isBlank()) {

            throw new RuntimeException(
                    "Mã QR không hợp lệ"
            );
        }

        if (
                deviceId == null ||
                        deviceId.isBlank()
        ) {

            throw new RuntimeException(
                    "Thiếu device id"
            );
        }

        // =====================================================
        // find qr
        // =====================================================

        QRCode qr = qrCodeRepository
                .findByCodeWithStall(code)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy mã QR"
                        )
                );

        if (
                !Boolean.TRUE.equals(
                        qr.getIsActive()
                )
        ) {

            throw new RuntimeException(
                    "Mã QR hiện đang bị khóa"
            );
        }

        Stall stall = qr.getStall();

        if (
                stall != null &&
                        !Boolean.TRUE.equals(
                                stall.getIsActive()
                        )
        ) {

            throw new RuntimeException(
                    "Gian hàng hiện không hoạt động"
            );
        }

        // =====================================================
        // anti spam
        // =====================================================

        LocalDateTime thirtySecondsAgo =
                LocalDateTime.now()
                        .minusSeconds(30);

        VisitEventType eventType =
                stall == null
                        ? VisitEventType.HOME_QR_SCAN
                        : VisitEventType.STALL_QR_SCAN;

        boolean duplicated =
                visitEventRepository
                        .existsByDeviceIdAndEventTypeAndCreatedAtAfter(
                                deviceId,
                                eventType,
                                thirtySecondsAgo
                        );

        if (duplicated) {

            throw new AppException(
                    ErrorCode.TOO_MANY_REQUESTS
            );
        }

        // =====================================================
        // build event
        // =====================================================

        VisitEvent event =
                VisitEvent.builder()

                        .deviceId(deviceId)

                        .stallId(
                                stall != null
                                        ? stall.getId()
                                        : null
                        )

                        .eventType(eventType)

                        .createdAt(LocalDateTime.now())

                        .build();

        // =====================================================
        // async save mysql
        // =====================================================

        visitEventAsyncService
                .logEventAsync(event);

        // =====================================================
        // mysql qr counter
        // =====================================================

        qrCodeRepository.incrementScanCount(
                qr.getId()
        );

        // =====================================================
        // realtime analytics
        // =====================================================

        analyticsService.increaseTotalQr();

        if (stall == null) {

            analyticsService
                    .addUniqueHomeVisitor(
                            deviceId
                    );
        }

        // =====================================================
        // websocket realtime
        // =====================================================

        try {

            analyticsRealtimeService
                    .pushRealtime();

        } catch (Exception e) {

            log.error(
                    "Push realtime failed",
                    e
            );
        }

        // =====================================================
        // redirect
        // =====================================================

        return stall == null
                ? "/home"
                : "/home/stall/" + stall.getId();
    }
}