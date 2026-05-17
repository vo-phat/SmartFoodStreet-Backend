package backend.enums;

import backend.common.exception.AppException;
import backend.common.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum TriggerType {
    GEOFENCE, // kích hoạt khi vào vùng
    DISTANCE; // trigger trước khi vào vùng

    @JsonCreator
    public static TriggerType from(String value) {
        System.out.println("RAW triggerType = [" + value + "]");

        if (value == null || value.trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_ENUM);
        }

        try {
            return TriggerType.valueOf(value.trim().toUpperCase());
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_ENUM);
        }
    }
}