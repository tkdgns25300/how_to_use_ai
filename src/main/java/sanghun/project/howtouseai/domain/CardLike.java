package sanghun.project.howtouseai.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "card_like")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CardLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    private Card card;

    @Column(name = "uuid", nullable = false, length = 255)
    private String uuid;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public CardLike(Card card, String uuid) {
        this.card = card;
        this.uuid = uuid;
        this.createdAt = LocalDateTime.now();
    }
} 