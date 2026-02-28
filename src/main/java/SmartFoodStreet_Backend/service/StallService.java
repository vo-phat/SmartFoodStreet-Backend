package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.common.exception.AppException;
import SmartFoodStreet_Backend.common.exception.ErrorCode;
import SmartFoodStreet_Backend.dto.stall.request.StallCreationRequest;
import SmartFoodStreet_Backend.dto.stall.request.StallUpdateRequest;
import SmartFoodStreet_Backend.dto.stall.response.StallResponse;
import SmartFoodStreet_Backend.entity.Account;
import SmartFoodStreet_Backend.entity.FoodStreet;
import SmartFoodStreet_Backend.entity.Stall;
import SmartFoodStreet_Backend.mapper.StallMapper;
import SmartFoodStreet_Backend.repository.AccountRepository;
import SmartFoodStreet_Backend.repository.FoodStreetRepository;
import SmartFoodStreet_Backend.repository.StallRepository;
import SmartFoodStreet_Backend.service.interfaces.IStall;
import SmartFoodStreet_Backend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StallService implements IStall {

    private final StallRepository repository;
    private final StallMapper mapper;
    private final AccountRepository accountRepository;
    private final FoodStreetRepository streetRepository;

    @Override
    @PreAuthorize("hasRole('VENDOR')")
    public StallResponse create(StallCreationRequest request) {

        Account vendor = accountRepository.findByUserName(
                SecurityUtils.getCurrentUsername()
        ).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));

        FoodStreet street = streetRepository.findById(request.getStreetId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        Stall stall = mapper.toEntity(request);
        stall.setVendor(vendor);
        stall.setStreet(street);

        return mapper.toResponse(repository.save(stall));
    }

    @Override
    @PreAuthorize("hasRole('VENDOR')")
    public StallResponse update(Long id, StallUpdateRequest request) {

        Stall stall = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        if (!stall.getVendor().getUserName()
                .equals(SecurityUtils.getCurrentUsername())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        mapper.update(stall, request);
        stall.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        return mapper.toResponse(repository.save(stall));
    }

    @Override
    @PreAuthorize("hasRole('VENDOR')")
    public void delete(Long id) {

        Stall stall = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        if (!stall.getVendor().getUserName()
                .equals(SecurityUtils.getCurrentUsername())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        repository.delete(stall);
    }

    @Override
    @PreAuthorize("hasRole('VENDOR')")
    public List<StallResponse> getMyStalls() {

        Account vendor = accountRepository.findByUserName(
                SecurityUtils.getCurrentUsername()
        ).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));

        return repository.findByVendorId(Long.valueOf(vendor.getId()))
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<StallResponse> getByStreet(Long streetId) {

        return repository.findByStreetIdAndIsActiveTrue(streetId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<StallResponse> getAllAdmin() {

        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public StallResponse getById(Long id) {

        return mapper.toResponse(
                repository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND))
        );
    }
}