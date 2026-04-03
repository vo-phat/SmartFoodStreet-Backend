package SmartFoodStreet_Backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stalls")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long streetId;
    Long vendorId;

    String name;
    String category;
    @Column(columnDefinition = "TEXT")
    String description;

    String latitude;
    String longitude;

    @Column(columnDefinition = "LONGTEXT")
    String image;

    @Column(columnDefinition = "LONGTEXT")
    String script;

    Boolean isActive;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}