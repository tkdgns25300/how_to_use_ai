package sanghun.project.howtouseai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LikeResponse {
    private boolean liked;
    private Long likesCount;
}