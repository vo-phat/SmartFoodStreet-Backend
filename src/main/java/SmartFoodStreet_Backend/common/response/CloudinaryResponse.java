package SmartFoodStreet_Backend.common.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CloudinaryResponse {
    String publicId;

    String url;

    String resourceType;
}
