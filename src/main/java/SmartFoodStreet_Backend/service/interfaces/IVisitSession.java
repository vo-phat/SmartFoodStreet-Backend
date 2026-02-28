package SmartFoodStreet_Backend.service.interfaces;

import SmartFoodStreet_Backend.dto.visitsesion.request.StartSessionRequest;
import SmartFoodStreet_Backend.dto.visitsesion.request.UpdateBudgetRequest;
import SmartFoodStreet_Backend.dto.visitsesion.response.VisitSessionResponse;

public interface IVisitSession {

    VisitSessionResponse start(StartSessionRequest request);

    VisitSessionResponse end(Long sessionId);

    VisitSessionResponse updateBudget(Long sessionId, UpdateBudgetRequest request);

    VisitSessionResponse getById(Long sessionId);
}