package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.common.exception.AppException;
import SmartFoodStreet_Backend.common.exception.ErrorCode;
import SmartFoodStreet_Backend.dto.stall.request.StallCreateRequest;
import SmartFoodStreet_Backend.dto.stall.response.StallResponse;
import SmartFoodStreet_Backend.entity.Stall;
import SmartFoodStreet_Backend.repository.StallRepository;
import SmartFoodStreet_Backend.service.interfaces.IStall;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StallService implements IStall {

    private final StallRepository repository;

    @Override
    @PreAuthorize("hasAuthority('STALL_CREATE')")
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
        stall.setLatitude(stallCreateRequest.getLatitude());
        stall.setLongitude(stallCreateRequest.getLongitude());
        stall.setImage(stallCreateRequest.getImage());
        stall.setIsActive(true);

        repository.save(stall);

        return map(stall);
    }

    @Override
    @PreAuthorize("hasAuthority('STALL_READ')")
    public StallResponse getById(Long id) {
        return map(find(id));
    }

    @Override
    @PreAuthorize("hasAuthority('STALL_READ')")
    public List<StallResponse> getByStreet(Long streetId) {
        return repository.findByStreetId(streetId)
                .stream().map(this::map).toList();
    }

    @Override
    @PreAuthorize("hasAuthority('STALL_UPDATE')")
    public StallResponse update(Long id, StallCreateRequest stallCreateRequest) {
        Stall stall = find(id);

        stall.setStreetId(stallCreateRequest.getStreetId());
        stall.setVendorId(stallCreateRequest.getVendorId());
        stall.setName(stallCreateRequest.getName());
        stall.setCategory(stallCreateRequest.getCategory());
        stall.setLatitude(stallCreateRequest.getLatitude());
        stall.setLongitude(stallCreateRequest.getLongitude());
        stall.setImage(stallCreateRequest.getImage());

        repository.save(stall);

        return map(stall);
    }

    @Override
    @PreAuthorize("hasAuthority('STALL_DELETE')")
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
                .name(stall.getName())
                .category(stall.getCategory())
                .latitude(stall.getLatitude())
                .longitude(stall.getLongitude())
                .image(stall.getImage())
                .isActive(stall.getIsActive())
                .build();
    }
}