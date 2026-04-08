package SmartFoodStreet_Backend.dto.stall.response;

import SmartFoodStreet_Backend.enums.AudioStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StallAudioResponse {
   private Boolean needDownload;

   private String audioUrl;

   private Long fileSize;

   private String audioHash;

   private AudioStatus status;

   private String message;
}