package SmartFoodStreet_Backend.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ICloudinary {
    Map uploadFile(MultipartFile file);

    Map deleteFile(String publicId, String resourceType);
}