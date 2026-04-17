package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.common.exception.AppException;
import SmartFoodStreet_Backend.common.exception.ErrorCode;
import SmartFoodStreet_Backend.common.response.CloudinaryResponse;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Upload file lên Cloudinary và trả về DTO UploadResponse
     *
     * @param file     MultipartFile upload lên
     * @param folder   Thư mục lưu trữ (ví dụ: "food", "stall")
     * @param publicId ID cho ảnh trên Cloudinary (nếu để trống Cloudinary sẽ tự
     *                 tạo)
     * @return UploadResponse (publicId, url, resourceType)
     */
    public CloudinaryResponse uploadFileImage(MultipartFile file, String folder, String publicId) {
        try {
            Map<String, Object> uploadOptions = ObjectUtils.asMap(
                    "resource_type", "image",
                    "folder", folder != null ? folder : "");

            // Nếu người dùng cung cấp publicId, sử dụng nó và cho phép ghi đè (overwrite)
            if (publicId != null && !publicId.trim().isEmpty()) {
                uploadOptions.put("public_id", publicId);
                uploadOptions.put("overwrite", true);
            }

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);

            String resultPublicId = (String) uploadResult.get("public_id");
            String url = (String) uploadResult.get("secure_url");
            String resourceTypeResult = (String) uploadResult.get("resource_type");

            return new CloudinaryResponse(resultPublicId, url, resourceTypeResult, file.getSize());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * Xóa file trên Cloudinary theo publicId
     *
     * @param publicId     ID của file trên Cloudinary
     * @param resourceType Loại resource: image, video, raw
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean deleteFile(String publicId, String resourceType) {
        try {
            Map deleteResult = cloudinary.uploader().destroy(publicId,
                    ObjectUtils.asMap("resource_type", resourceType));
            String resultStatus = (String) deleteResult.get("result");
            return "ok".equals(resultStatus);
        } catch (Exception e) {
            throw new AppException(ErrorCode.FILE_DELETE_FAILED);
        }
    }

    public boolean deleteFile(String publicId) {
        return deleteFile(publicId, "image");
    }

    public boolean deleteByUrl(String url) {
        String publicId = extractPublicId(url);
        if (publicId != null) {
            return deleteFile(publicId);
        }
        return false;
    }

    public String extractPublicId(String url) {
        if (url == null || url.isEmpty() || !url.contains("cloudinary.com"))
            return null;
        try {
            // URL format:
            // https://res.cloudinary.com/<cloud_name>/image/upload/v<version>/<folder>/<public_id>.<extension>
            String folderAndId = url.substring(url.lastIndexOf("/upload/") + 8);
            // Skip the version if present (v123456789/)
            if (folderAndId.startsWith("v") && folderAndId.substring(1, 11).matches("\\d+")) {
                folderAndId = folderAndId.substring(folderAndId.indexOf("/") + 1);
            }
            // Remove the file extension
            return folderAndId.substring(0, folderAndId.lastIndexOf("."));
        } catch (Exception e) {
            log.error("Failed to extract publicId from URL: " + url, e);
            return null;
        }
    }

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
                            "format", "mp3"));

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
            params.put("public_id", publicId); // Đặt tên cố định: vd stall_1_ko
            params.put("overwrite", true); // Lệnh ghi đè (Xóa cũ, đè mới)
            params.put("format", "mp3"); // Đảm bảo định dạng chuẩn mp3

            if (fileBytes == null || fileBytes.length == 0) {
                throw new IllegalArgumentException("Dữ liệu file âm thanh rỗng");
            }

            Map uploadResult = cloudinary.uploader().upload(fileBytes, params);

            // Tận dụng hàm map() có sẵn để parse Map thành DTO
            return map(uploadResult);

        } catch (Exception e) {
            log.error("Upload & overwrite audio error for publicId {}: {}", publicId, e.getMessage());
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * * Upload image/file (giữ lại)
     */
    public CloudinaryResponse uploadFile(byte[] data) {

        try {
            Map uploadResult = cloudinary.uploader().upload(
                    data,
                    ObjectUtils.asMap("resource_type", "auto"));

            return map(uploadResult);

        } catch (Exception e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    // /**
    // * Delete
    // */
    // public boolean deleteFile(String publicId) {

    // try {
    // Map result = cloudinary.uploader().destroy(
    // publicId,ObjectUtils.asMap("resource_type", "video")
    // );

    // return "ok".equals(result.get("result"));

    // } catch (Exception e) {
    // throw new AppException(ErrorCode.FILE_DELETE_FAILED);
    // }
    // }

    // /**
    // * Mapper chung chuyển đổi Map của Cloudinary sang CloudinaryResponse
    // */
    private CloudinaryResponse map(Map uploadResult) {

        return new CloudinaryResponse(
                (String) uploadResult.get("public_id"),
                (String) uploadResult.get("secure_url"),
                (String) uploadResult.get("resource_type"),
                ((Number) uploadResult.get("bytes")).longValue());
    }

}
