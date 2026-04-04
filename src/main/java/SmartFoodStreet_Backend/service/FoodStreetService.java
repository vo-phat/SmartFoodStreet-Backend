package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.common.exception.AppException;
import SmartFoodStreet_Backend.common.exception.ErrorCode;
import SmartFoodStreet_Backend.dto.street.request.FoodStreetCreationRequest;
import SmartFoodStreet_Backend.dto.street.request.FoodStreetUpdateRequest;
import SmartFoodStreet_Backend.dto.street.response.FoodStreetResponse;
import SmartFoodStreet_Backend.entity.FoodStreet;
import SmartFoodStreet_Backend.mapper.FoodStreetMapper;
import SmartFoodStreet_Backend.repository.FoodStreetRepository;
import SmartFoodStreet_Backend.service.interfaces.IFoodStreet;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodStreetService implements IFoodStreet {

    private final FoodStreetRepository repository;
    private final FoodStreetMapper mapper;

    @Override
    @PreAuthorize("hasAuthority('STREET_CREATE')")
    public FoodStreetResponse create(FoodStreetCreationRequest request) {
        return mapper.toResponse(
                repository.save(mapper.toEntity(request))
        );
    }

    @Override
    @PreAuthorize("hasAuthority('STREET_READ')")
    public List<FoodStreetResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<FoodStreetResponse> getActive() {
        return repository.findByIsActiveTrue()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @PreAuthorize("hasAuthority('STREET_READ')")
    public FoodStreetResponse getById(Long id) {
        return mapper.toResponse(
                repository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND))
        );
    }

    @Override
    @PreAuthorize("hasAuthority('STREET_UPDATE')")
    public FoodStreetResponse update(Long id, FoodStreetUpdateRequest request) {

        FoodStreet entity = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        mapper.update(entity, request);
        entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @PreAuthorize("hasAuthority('STREET_DELETE')")
    public void delete(Long id) {
        repository.deleteById(id);
    }
}