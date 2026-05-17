package backend.dto.stall.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StallTranslationResponse {
    Long id;

    Long stallId;

    String languageCode;

    String name;

    String ttsScript;

    String audioUrl;

    Long fileSize;

    String audioHash;

    String audioStatus;
}