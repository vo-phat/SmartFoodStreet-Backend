package SmartFoodStreet_Backend.service.interfaces;

import SmartFoodStreet_Backend.dto.voucher.request.GenerateVoucherRequest;
import SmartFoodStreet_Backend.dto.voucher.response.VoucherResponse;

public interface IVoucher {

    VoucherResponse generate(GenerateVoucherRequest request);

    VoucherResponse redeem(String code);
}