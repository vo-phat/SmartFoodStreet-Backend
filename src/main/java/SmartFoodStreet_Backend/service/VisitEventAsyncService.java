package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.entity.VisitEvent;
import SmartFoodStreet_Backend.repository.VisitEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VisitEventAsyncService {

   private final VisitEventRepository visitEventRepository;

   // @Async("qrExecutor")
   public void logEventAsync(VisitEvent event) {
      try {
         visitEventRepository.save(event);
      } catch (Exception e) {
         System.out.println("Lỗi log Event: " + e.getMessage());
      }
   }
}
