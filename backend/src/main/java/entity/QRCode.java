package SmartFoodStreet_Backend.entity;

import java.time.LocalDateTime;

import lombok.Builder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "qr_codes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QRCode {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   private String code;

   @ManyToOne
   @JoinColumn(name = "stall_id", nullable = true)
   private Stall stall;

   private String name;

   @Column(name = "is_active")
   private Boolean isActive;

   @Column(name = "scan_count")
   private Integer scanCount;

   private LocalDateTime createdAt;
   private LocalDateTime updatedAt;
}
