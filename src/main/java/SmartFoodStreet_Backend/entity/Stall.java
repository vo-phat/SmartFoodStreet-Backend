package SmartFoodStreet_Backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "stalls")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "street_id")
    FoodStreet street;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    Account vendor;

    String name;
    String category;

    Double avgPrice;
    Double minPrice;
    Double maxPrice;

    Double latitude;
    Double longitude;

    Integer radius;
    Integer priority;
    Integer cooldownSeconds;

    Double recommendationScore;

    Boolean isActive;

    @Column(name = "created_at")
    Timestamp createdAt;

    @Column(name = "updated_at")
    Timestamp updatedAt;
}