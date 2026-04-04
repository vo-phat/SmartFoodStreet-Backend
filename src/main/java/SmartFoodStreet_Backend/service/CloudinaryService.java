package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.common.exception.AppException;
import SmartFoodStreet_Backend.common.exception.ErrorCode;
import SmartFoodStreet_Backend.common.response.CloudinaryResponse;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    /**
     * Upload audio từ byte[] (Tạo mới không ghi đè)
     */
    public CloudinaryResponse uploadAudio(byte[] data, String fileName) {

        try {
            Map uploadResult = cloudinary.uploader().upload(
                    data,
                    ObjectUtils.asMap(
                            "resource_type", "video",
                            "public_id", fileName,
                            "format", "mp3"
                    )
            );

            return map(uploadResult);

        } catch (Exception e) {
            log.error("Upload audio error: {}", e.getMessage());
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * Upload audio có chức năng GHI ĐÈ (Dùng cho đa ngôn ngữ TTS)
     */
    public CloudinaryResponse uploadAudioWithOverwrite(byte[] fileBytes, String publicId) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("resource_type", "video"); // Cloudinary coi Audio là Video
            params.put("public_id", publicId);    // Đặt tên cố định: vd stall_1_ko
            params.put("overwrite", true);        // Lệnh ghi đè (Xóa cũ, đè mới)
            params.put("format", "mp3");          // Đảm bảo định dạng chuẩn mp3

            Map uploadResult = cloudinary.uploader().upload(fileBytes, params);

            // Tận dụng hàm map() có sẵn để parse Map thành DTO
            return map(uploadResult);

        } catch (Exception e) {
            log.error("Upload & overwrite audio error for publicId {}: {}", publicId, e.getMessage());
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * Upload image/file (giữ lại)
     */
    public CloudinaryResponse uploadFile(byte[] data) {

        try {
            Map uploadResult = cloudinary.uploader().upload(
                    data,
                    ObjectUtils.asMap("resource_type", "auto")
            );

            return map(uploadResult);

        } catch (Exception e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * Delete
     */
    public boolean deleteFile(String publicId) {

        try {
            Map result = cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap("resource_type", "video")
            );

            return "ok".equals(result.get("result"));

        } catch (Exception e) {
            throw new AppException(ErrorCode.FILE_DELETE_FAILED);
        }
    }

    /**
     * Mapper chung chuyển đổi Map của Cloudinary sang CloudinaryResponse
     */
    private CloudinaryResponse map(Map uploadResult) {

        return new CloudinaryResponse(
                (String) uploadResult.get("public_id"),
                (String) uploadResult.get("secure_url"),
                (String) uploadResult.get("resource_type"),
                ((Number) uploadResult.get("bytes")).longValue()
        );
    }
}