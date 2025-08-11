package sanghun.project.howtouseai.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sanghun.project.howtouseai.dto.ApiResponse;
import sanghun.project.howtouseai.dto.ResponseHelper;
import sanghun.project.howtouseai.service.CardLikeService;
import sanghun.project.howtouseai.dto.LikeResponse;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/cards/{cardId}/like")
@RequiredArgsConstructor
public class CardLikeController {

    private final CardLikeService cardLikeService;

    @PostMapping
    public ResponseEntity<ApiResponse<LikeResponse>> toggleLike(
            @PathVariable Long cardId,
            @RequestBody Map<String, String> request) {
        
        String uuid = request.get("uuid");
        log.info("좋아요 토글 API 호출: cardId={}, uuid={}", cardId, uuid);
        
        try {
            LikeResponse likeResponse = cardLikeService.toggleLike(cardId, uuid);
            
            ApiResponse<LikeResponse> response = ResponseHelper.success(
                likeResponse,
                "좋아요 상태가 성공적으로 변경되었습니다."
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("좋아요 토글 중 오류 발생: {}", e.getMessage(), e);
            throw e; // GlobalExceptionHandler에서 처리
        }
    }
} 