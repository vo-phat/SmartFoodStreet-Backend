package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.common.exception.AppException;
import SmartFoodStreet_Backend.common.exception.ErrorCode;
import SmartFoodStreet_Backend.common.response.CloudinaryResponse;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
     * @param publicId ID cho ảnh trên Cloudinary (nếu để trống Cloudinary sẽ tự tạo)
     * @return UploadResponse (publicId, url, resourceType)
     */
    public CloudinaryResponse uploadFile(MultipartFile file, String folder, String publicId) {
        try {
            Map<String, Object> uploadOptions = ObjectUtils.asMap(
                    "resource_type", "auto",
                    "folder", folder != null ? folder : ""
            );

            // Nếu người dùng cung cấp publicId, sử dụng nó và cho phép ghi đè (overwrite)
            if (publicId != null && !publicId.trim().isEmpty()) {
                uploadOptions.put("public_id", publicId);
                uploadOptions.put("overwrite", true);
            }

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);

            String resultPublicId = (String) uploadResult.get("public_id");
            String url = (String) uploadResult.get("secure_url");
            String resourceTypeResult = (String) uploadResult.get("resource_type");

            return new CloudinaryResponse(resultPublicId, url, resourceTypeResult);
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
        if (url == null || url.isEmpty() || !url.contains("cloudinary.com")) return null;
        try {
            // URL format: https://res.cloudinary.com/<cloud_name>/image/upload/v<version>/<folder>/<public_id>.<extension>
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
}
