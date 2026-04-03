package SmartFoodStreet_Backend.dto.food.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodResponse {
    Long id;

    String name;

    BigDecimal price;

    String description;

    String image;

    Boolean isAvailable;
}