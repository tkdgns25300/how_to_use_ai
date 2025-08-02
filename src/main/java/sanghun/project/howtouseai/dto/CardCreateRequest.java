package sanghun.project.howtouseai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardCreateRequest {

    @NotBlank(message = "카드 제목은 필수입니다")
    @Size(max = 255, message = "카드 제목은 255자 이하여야 합니다")
    private String title;

    @NotNull(message = "카테고리 ID는 필수입니다")
    private Long categoryId;

    private String tags;

    private String situation;

    private String usageExamples;

    private String content;

    @NotBlank(message = "디바이스 UUID는 필수입니다")
    private String uuid;
} 