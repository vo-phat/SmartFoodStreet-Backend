package SmartFoodStreet_Backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "foods")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "stall_id")
    Long stallId;

    String name;

    BigDecimal price;

    String description;

    String image;

    Boolean isAvailable;

    LocalDateTime createdAt;
}