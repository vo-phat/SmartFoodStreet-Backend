package backend.dto.visitsesion.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitEventRequest {
    private Long stallId;
    private String eventType; // AUDIO_COMPLETE, QR_SCAN, v.v.
    private Long sessionId;
    private String qrCode;
}