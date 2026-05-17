package backend.service;

import backend.common.exception.AppException;
import backend.common.exception.ErrorCode;
import backend.dto.street.request.FoodStreetCreationRequest;
import backend.dto.street.request.FoodStreetUpdateRequest;
import backend.dto.street.response.FoodStreetResponse;
import backend.entity.FoodStreet;
import backend.mapper.FoodStreetMapper;
import backend.repository.FoodStreetRepository;
import backend.service.interfaces.IFoodStreet;
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
    @PreAuthorize("hasRole('ADMIN')")
    public FoodStreetResponse create(FoodStreetCreationRequest request) {
        return mapper.toResponse(
                repository.save(mapper.toEntity(request))
        );
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
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
    public FoodStreetResponse getById(Long id) {
        return mapper.toResponse(
                repository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND))
        );
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public FoodStreetResponse update(Long id, FoodStreetUpdateRequest request) {

        FoodStreet entity = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        mapper.update(entity, request);
        entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long id) {
        repository.deleteById(id);
    }
}