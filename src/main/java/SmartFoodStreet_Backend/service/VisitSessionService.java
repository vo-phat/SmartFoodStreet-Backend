package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.common.exception.AppException;
import SmartFoodStreet_Backend.common.exception.ErrorCode;
import SmartFoodStreet_Backend.dto.visitsesion.request.StartSessionRequest;
import SmartFoodStreet_Backend.dto.visitsesion.request.UpdateBudgetRequest;
import SmartFoodStreet_Backend.dto.visitsesion.response.VisitSessionResponse;
import SmartFoodStreet_Backend.entity.FoodStreet;
import SmartFoodStreet_Backend.entity.VisitSession;
import SmartFoodStreet_Backend.mapper.VisitSessionMapper;
import SmartFoodStreet_Backend.repository.FoodStreetRepository;
import SmartFoodStreet_Backend.repository.VisitSessionRepository;
import SmartFoodStreet_Backend.service.interfaces.IVisitSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class VisitSessionService implements IVisitSession {

    private final VisitSessionRepository repository;
    private final VisitSessionMapper mapper;
    private final FoodStreetRepository streetRepository;

    @Override
    public VisitSessionResponse start(StartSessionRequest request) {

        FoodStreet street = streetRepository.findById(request.getStreetId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        VisitSession session = mapper.toEntity(request);

        session.setStreet(street);
        session.setBudgetRemaining(request.getBudgetInitial());

        return mapper.toResponse(repository.save(session));
    }

    @Override
    public VisitSessionResponse end(Long sessionId) {

        VisitSession session = repository.findById(sessionId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        session.setEndedAt(new Timestamp(System.currentTimeMillis()));

        return mapper.toResponse(repository.save(session));
    }

    @Override
    public VisitSessionResponse updateBudget(Long sessionId, UpdateBudgetRequest request) {

        VisitSession session = repository.findById(sessionId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        session.setBudgetRemaining(request.getBudgetRemaining());

        return mapper.toResponse(repository.save(session));
    }

    @Override
    public VisitSessionResponse getById(Long sessionId) {

        return mapper.toResponse(
                repository.findById(sessionId)
                        .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND))
        );
    }
}