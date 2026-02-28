package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.common.exception.AppException;
import SmartFoodStreet_Backend.common.exception.ErrorCode;
import SmartFoodStreet_Backend.dto.voucher.request.GenerateVoucherRequest;
import SmartFoodStreet_Backend.dto.voucher.response.VoucherResponse;
import SmartFoodStreet_Backend.entity.VisitEvent;
import SmartFoodStreet_Backend.entity.Voucher;
import SmartFoodStreet_Backend.mapper.VoucherMapper;
import SmartFoodStreet_Backend.repository.VisitEventRepository;
import SmartFoodStreet_Backend.repository.VoucherRepository;
import SmartFoodStreet_Backend.service.interfaces.IVoucher;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VoucherService implements IVoucher {

    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;
    private final VisitEventRepository eventRepository;

    @Override
    public VoucherResponse generate(GenerateVoucherRequest request) {

        Optional<Voucher> existing =
                voucherRepository
                        .findBySessionIdAndStallIdAndIsRedeemedFalse(
                                request.getSessionId(),
                                request.getStallId()
                        );

        if (existing.isPresent())
            return voucherMapper.toResponse(existing.get());

        String code = generateCode();

        Voucher voucher = voucherMapper.toVoucher(
                request.getSessionId(),
                request.getStallId(),
                code
        );

        voucherRepository.save(voucher);

        logEvent(request.getSessionId(),
                request.getStallId(),
                VisitEvent.EventType.VOUCHER_GENERATED);

        return voucherMapper.toResponse(voucher);
    }

    @Override
    @PreAuthorize("hasRole('VENDOR')")
    public VoucherResponse redeem(String code) {

        Voucher voucher = voucherRepository.findByVoucherCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        if (Boolean.TRUE.equals(voucher.getIsRedeemed()))
            throw new AppException(ErrorCode.REDEEM_REQUEST_INVALID);

        voucher.setIsRedeemed(true);
        voucher.setRedeemedAt(LocalDateTime.now());

        voucherRepository.save(voucher);

        logEvent(voucher.getSessionId(),
                voucher.getStallId(),
                VisitEvent.EventType.VOUCHER_REDEEMED);

        return voucherMapper.toResponse(voucher);
    }

    private void logEvent(Long sessionId,
                          Long stallId,
                          VisitEvent.EventType type) {

        VisitEvent event = VisitEvent.builder()
                .sessionId(sessionId)
                .stallId(stallId)
                .eventType(type)
                .eventTime(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        eventRepository.save(event);
    }

    private String generateCode() {
        return "VCH-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }
}