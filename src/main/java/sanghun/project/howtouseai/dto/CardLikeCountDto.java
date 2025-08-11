package sanghun.project.howtouseai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CardLikeCountDto {
    private Long cardId;
    private Long likeCount;
}