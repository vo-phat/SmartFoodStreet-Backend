package SmartFoodStreet_Backend.entity;

import SmartFoodStreet_Backend.enums.AudioStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stall_translations", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"stall_id", "language_code"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StallTranslation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stall_id", nullable = false)
    private Long stallId;

    @Column(name = "language_code", nullable = false, length = 10)
    private String languageCode;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "tts_script", columnDefinition = "TEXT")
    private String ttsScript;

    @Column(name = "audio_url", length = 255)
    private String audioUrl;

    @Column(name = "file_size")
    private Long fileSize = 0L;

    @Column(name = "audio_hash", length = 64)
    private String audioHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "audio_status", columnDefinition = "ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'ERROR') DEFAULT 'PENDING'")
    private AudioStatus audioStatus = AudioStatus.PENDING;
}