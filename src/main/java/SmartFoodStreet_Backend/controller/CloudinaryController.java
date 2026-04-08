package SmartFoodStreet_Backend.controller;

import SmartFoodStreet_Backend.common.response.ApiResponse;
import SmartFoodStreet_Backend.common.response.CloudinaryResponse;
import SmartFoodStreet_Backend.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/cloudinary")
@RequiredArgsConstructor
public class CloudinaryController {

    private final CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public ApiResponse<CloudinaryResponse> uploadFile(@RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String folder,
            @RequestParam(required = false) String publicId) {
        ApiResponse<CloudinaryResponse> apiResponse = new ApiResponse<>();

        apiResponse.setResult(cloudinaryService.uploadFileImage(file, folder, publicId));

        return apiResponse;
    }

    @DeleteMapping("/delete")
    public ApiResponse<CloudinaryResponse> deleteFile(@RequestParam String publicId,
            @RequestParam(defaultValue = "image") String resourceType) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setResult(cloudinaryService.deleteFile(publicId, resourceType));

        return apiResponse;
    }
}
