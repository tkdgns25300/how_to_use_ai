package sanghun.project.howtouseai.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sanghun.project.howtouseai.dto.ApiResponse;
import sanghun.project.howtouseai.dto.ResponseHelper;
import sanghun.project.howtouseai.service.CardLikeService;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardLikeController {

    private final CardLikeService cardLikeService;

    @PostMapping("/{cardId}/like")
    public ResponseEntity<ApiResponse<String>> addLike(
            @PathVariable Long cardId,
            @RequestBody Map<String, String> request) {
        
        String uuid = request.get("uuid");
        log.info("좋아요 추가 API 호출: cardId={}, uuid={}", cardId, uuid);
        
        try {
            cardLikeService.addLike(cardId, uuid);
            
            ApiResponse<String> response = ResponseHelper.success(
                "좋아요 완료",
                "카드에 좋아요를 성공적으로 추가했습니다."
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("좋아요 추가 중 오류 발생: {}", e.getMessage(), e);
            throw e; // GlobalExceptionHandler에서 처리
        }
    }

    @DeleteMapping("/{cardId}/like")
    public ResponseEntity<ApiResponse<String>> removeLike(
            @PathVariable Long cardId,
            @RequestParam("uuid") String uuid) {
        
        log.info("좋아요 취소 API 호출: cardId={}, uuid={}", cardId, uuid);
        
        try {
            cardLikeService.removeLike(cardId, uuid);
            
            ApiResponse<String> response = ResponseHelper.success(
                "좋아요 취소 완료",
                "카드의 좋아요를 성공적으로 취소했습니다."
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("좋아요 취소 중 오류 발생: {}", e.getMessage(), e);
            throw e; // GlobalExceptionHandler에서 처리
        }
    }
} 