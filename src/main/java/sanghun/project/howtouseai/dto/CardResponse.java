package sanghun.project.howtouseai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
} 