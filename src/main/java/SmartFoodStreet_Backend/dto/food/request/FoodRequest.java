package SmartFoodStreet_Backend.dto.food.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodRequest {
    @NotNull
    Long stallId;

    @NotBlank
    String name;

    @NotNull
    @DecimalMin("0.0")
    BigDecimal price;

    String description;

    String image;
}