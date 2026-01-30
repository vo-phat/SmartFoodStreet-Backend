package SmartFoodStreet_Backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "username")
    String userName;

    String password;

    @Column(name = "full_name")
    String fullName;

    String email;

    @Enumerated(EnumType.STRING)
    Role role = Role.STAFF;

    @Column(name = "is_active")
    Boolean isActive = true;

    @Column(name = "created_at")
    Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    public enum Role {
        ADMIN, STAFF
    }

}
