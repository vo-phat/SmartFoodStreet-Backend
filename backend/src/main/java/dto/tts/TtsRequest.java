package SmartFoodStreet_Backend.dto.tts;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TtsRequest {
    private String text;
}