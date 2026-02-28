package SmartFoodStreet_Backend.mapper;

import SmartFoodStreet_Backend.dto.voucher.response.VoucherResponse;
import SmartFoodStreet_Backend.entity.Voucher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VoucherMapper {

    VoucherResponse toResponse(Voucher voucher);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "voucherCode", source = "code")
    @Mapping(target = "discountPercent", constant = "10")
    @Mapping(target = "estimatedValue", constant = "20000.0")
    @Mapping(target = "isRedeemed", constant = "false")
    @Mapping(target = "redeemedAt", ignore = true)
    @Mapping(target = "createdAt",
            expression = "java(java.time.LocalDateTime.now())")
    Voucher toVoucher(Long sessionId,
                      Long stallId,
                      String code);
}