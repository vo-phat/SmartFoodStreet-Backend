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
     * @param file MultipartFile upload lên
     * @return UploadResponse (publicId, url, resourceType)
     */
    public CloudinaryResponse uploadFile(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto"));

            String publicId = (String) uploadResult.get("public_id");
            String url = (String) uploadResult.get("secure_url");
            String resourceTypeResult = (String) uploadResult.get("resource_type");

            return new CloudinaryResponse(publicId, url, resourceTypeResult);
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

    public String extractPublicId(String url) {
        if (url == null || url.isEmpty()) return null;
        try {
            String[] parts = url.split("/");
            String lastPart = parts[parts.length - 1]; // ví dụ: abcxyz.jpg
            return lastPart.substring(0, lastPart.lastIndexOf(".")); // abcxyz
        } catch (Exception e) {
            return null;
        }
    }
}
