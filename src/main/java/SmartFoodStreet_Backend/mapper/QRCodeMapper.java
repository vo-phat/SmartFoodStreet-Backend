package SmartFoodStreet_Backend.mapper;

import SmartFoodStreet_Backend.dto.qrcode.QRCodeResponse;
import SmartFoodStreet_Backend.entity.QRCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QRCodeMapper {

    @Mapping(target = "stallId", source = "stall.id")
    @Mapping(target = "stallName", source = "stall.name")
    QRCodeResponse toResponse(QRCode entity);

    List<QRCodeResponse> toResponseList(List<QRCode> entities);
}
