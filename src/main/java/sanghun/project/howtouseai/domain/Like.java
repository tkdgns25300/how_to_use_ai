package sanghun.project.howtouseai.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "`like`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    private Card card;

    @Column(name = "device_key", nullable = false, length = 255)
    private String deviceKey;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Like(Card card, String deviceKey) {
        this.card = card;
        this.deviceKey = deviceKey;
        this.createdAt = LocalDateTime.now();
    }
} 