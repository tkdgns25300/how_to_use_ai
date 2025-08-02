package sanghun.project.howtouseai.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardUpdateRequest {

    @Size(max = 255, message = "카드 제목은 255자 이하여야 합니다")
    private String title;

    private Long categoryId;

    private String tags;

    private String situation;

    private String usageExamples;

    private String content;

    private String uuid; // 수정 권한 확인용
} 