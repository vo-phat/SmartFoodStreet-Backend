package SmartFoodStreet_Backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Entity
@Table(name = "food_streets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FoodStreet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    @Column(columnDefinition = "TEXT")
    String description;

    String address;

    String city;

    Double latitude;

    Double longitude;

    Boolean isActive = true;

    @Column(name = "created_at")
    Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_at")
    Timestamp updatedAt = new Timestamp(System.currentTimeMillis());
}