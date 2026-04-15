package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.common.exception.AppException;
import SmartFoodStreet_Backend.common.exception.ErrorCode;
import SmartFoodStreet_Backend.dto.stall.request.StallCreateRequest;
import SmartFoodStreet_Backend.dto.stall.response.StallResponse;
import SmartFoodStreet_Backend.entity.Stall;
import SmartFoodStreet_Backend.repository.StallRepository;
import SmartFoodStreet_Backend.service.interfaces.IStall;
import SmartFoodStreet_Backend.service.interfaces.IStallTranslation;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StallService implements IStall {

    private final StallRepository repository;
    private final CloudinaryService cloudinaryService;
    private final IStallTranslation stallTranslationService;


    @Override
    public StallResponse create(StallCreateRequest stallCreateRequest) {

        boolean exists = repository
                .existsByStreetIdAndNameIgnoreCase(stallCreateRequest.getStreetId(), stallCreateRequest.getName());

        if (exists)
            throw new AppException(ErrorCode.STALL_ALREADY_EXISTS);

        Stall stall = new Stall();
        stall.setStreetId(stallCreateRequest.getStreetId());
        stall.setVendorId(stallCreateRequest.getVendorId());
        stall.setName(stallCreateRequest.getName());
        stall.setCategory(stallCreateRequest.getCategory());
        stall.setDescription(stallCreateRequest.getDescription());
        stall.setLatitude(stallCreateRequest.getLatitude());
        stall.setLongitude(stallCreateRequest.getLongitude());
        stall.setImage(stallCreateRequest.getImage());
        stall.setScript(stallCreateRequest.getScript());
        stall.setIsActive(false);

        repository.save(stall);
 
        // Lưu script sang StallTranslation (Xoá cũ nếu có, tạo mới bản gốc vi)
        if (stall.getScript() != null) {
            stallTranslationService.deleteAllByStall(stall.getId());
            stallTranslationService.saveOrUpdate(stall.getId(), "vi", stall.getScript());
        }
 
        return map(stall);
    }

    @Override
    public StallResponse getById(Long id) {
        return map(find(id));
    }

    @Override
    public List<StallResponse> getByStreet(Long streetId) {
        return repository.findByStreetIdAndIsActiveTrue(streetId)
                .stream().map(this::map).toList();
    }

    @Override
    public List<StallResponse> getAllActive() {
        return repository.findByIsActiveTrue()
                .stream().map(this::map).toList();
    }

    @Override
    public List<StallResponse> getAll() {
        return repository.findAll()
                .stream().map(this::map).toList();
    }

    @Override
    public StallResponse getByVendor(Long vendorId) {
        return repository.findByVendorId(vendorId).stream().findFirst()
                .map(this::map)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Override
    public StallResponse update(Long id, StallCreateRequest stallCreateRequest) {
        Stall stall = find(id);

        if (stallCreateRequest.getStreetId() != null) stall.setStreetId(stallCreateRequest.getStreetId());
        if (stallCreateRequest.getVendorId() != null) stall.setVendorId(stallCreateRequest.getVendorId());
        if (stallCreateRequest.getName() != null) stall.setName(stallCreateRequest.getName());
        if (stallCreateRequest.getCategory() != null) stall.setCategory(stallCreateRequest.getCategory());
        if (stallCreateRequest.getDescription() != null) stall.setDescription(stallCreateRequest.getDescription());
        if (stallCreateRequest.getLatitude() != null) stall.setLatitude(stallCreateRequest.getLatitude());
        if (stallCreateRequest.getLongitude() != null) stall.setLongitude(stallCreateRequest.getLongitude());

        if (stallCreateRequest.getImage() != null && !stallCreateRequest.getImage().equals(stall.getImage())) {
            // Delete the old image from Cloudinary if it exists
            if (stall.getImage() != null) {
                cloudinaryService.deleteByUrl(stall.getImage());
            }
            stall.setImage(stallCreateRequest.getImage());
        }

        boolean scriptChanged = stallCreateRequest.getScript() != null && !stallCreateRequest.getScript().equals(stall.getScript());
 
        if (stallCreateRequest.getScript() != null) stall.setScript(stallCreateRequest.getScript());
        if (stallCreateRequest.getIsActive() != null) stall.setIsActive(stallCreateRequest.getIsActive());

        repository.save(stall);
 
        // Đồng bộ script sang StallTranslation (Chỉ xoá khi script gốc thay đổi)
        if (scriptChanged) {
            stallTranslationService.deleteAllByStall(stall.getId());
            stallTranslationService.saveOrUpdate(stall.getId(), "vi", stall.getScript());
        }
 
        return map(stall);
    }

    @Override
    public void delete(Long id) {
        Stall stall = find(id);
        stall.setIsActive(false);
        repository.save(stall);
    }

    private Stall find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    private StallResponse map(Stall stall) {
        return StallResponse.builder()
                .id(stall.getId())
                .streetId(stall.getStreetId())
                .vendorId(stall.getVendorId())
                .name(stall.getName())
                .category(stall.getCategory())
                .description(stall.getDescription())
                .latitude(stall.getLatitude())
                .longitude(stall.getLongitude())
                .image(stall.getImage())
                .script(stall.getScript())
                .isActive(stall.getIsActive())
                .radius(stall.getTriggerConfig() != null ? stall.getTriggerConfig().getRadius() : null)
                .build();
    }
}