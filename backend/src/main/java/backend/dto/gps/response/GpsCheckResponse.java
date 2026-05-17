package backend.dto.gps.response;

import backend.dto.stall.response.StallResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GpsCheckResponse {

    private StallResponse triggeredStall;
}