package sanghun.project.howtouseai.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "card")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, length = 255)
    private String uuid;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags;

    @Column(name = "situation", columnDefinition = "TEXT")
    private String situation;

    @Column(name = "usage_examples", columnDefinition = "TEXT")
    private String usageExamples;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Card(String uuid, String title, Category category, String tags, String situation, 
                String usageExamples, String content) {
        this.uuid = uuid;
        this.title = title;
        this.category = category;
        this.tags = tags;
        this.situation = situation;
        this.usageExamples = usageExamples;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public void updateInfo(String title, Category category, String tags, String situation, 
                          String usageExamples, String content) {
        this.title = title;
        this.category = category;
        this.tags = tags;
        this.situation = situation;
        this.usageExamples = usageExamples;
        this.content = content;
    }
}