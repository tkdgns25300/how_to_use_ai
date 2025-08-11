package sanghun.project.howtouseai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardResponse {
    private Long id;
    private String uuid;
    private String title;
    private CategoryResponse category;
    private String tags;
    private String situation;
    private String usageExamples;
    private String content;
    private LocalDateTime createdAt;
    private Long likesCount;  // 좋아요 수
    private boolean likedByUser; // 현재 사용자의 좋아요 여부
    private List<String> likedUserUuids;  // 좋아요한 사용자 UUID 목록
} 