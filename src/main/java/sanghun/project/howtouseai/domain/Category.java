package sanghun.project.howtouseai.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "icon_url", nullable = false, length = 500)
    private String iconUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Category(String name, String iconUrl) {
        this.name = name;
        this.iconUrl = iconUrl;
        this.createdAt = LocalDateTime.now();
    }
} 