package sanghun.project.howtouseai.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sanghun.project.howtouseai.dto.ApiResponse;
import sanghun.project.howtouseai.dto.CardCreateRequest;
import sanghun.project.howtouseai.dto.CardResponse;
import sanghun.project.howtouseai.dto.CardUpdateRequest;
import sanghun.project.howtouseai.dto.ResponseHelper;
import sanghun.project.howtouseai.service.CardService;

@Slf4j
@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<ApiResponse<CardResponse>> createCard(
            @RequestBody CardCreateRequest request) {
        
        log.info("카드 생성 API 호출: title={}, categoryId={}", request.getTitle(), request.getCategoryId());
        
        try {
            CardResponse cardResponse = cardService.createCard(request);
            
            ApiResponse<CardResponse> response = ResponseHelper.success(
                cardResponse,
                "카드가 성공적으로 생성되었습니다."
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("카드 생성 중 오류 발생: {}", e.getMessage(), e);
            throw e; // GlobalExceptionHandler에서 처리
        }
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<ApiResponse<CardResponse>> updateCard(
            @PathVariable Long cardId,
            @RequestBody CardUpdateRequest request) {
        
        log.info("카드 수정 API 호출: cardId={}, title={}", cardId, request.getTitle());
        
        try {
            CardResponse cardResponse = cardService.updateCard(cardId, request);
            
            ApiResponse<CardResponse> response = ResponseHelper.success(
                cardResponse,
                "카드가 성공적으로 수정되었습니다."
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("카드 수정 중 오류 발생: {}", e.getMessage(), e);
            throw e; // GlobalExceptionHandler에서 처리
        }
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<ApiResponse<String>> deleteCard(
            @PathVariable Long cardId,
            @RequestParam("uuid") String uuid) {
        
        log.info("카드 삭제 API 호출: cardId={}, uuid={}", cardId, uuid);
        
        try {
            cardService.deleteCard(cardId, uuid);
            
            ApiResponse<String> response = ResponseHelper.success(
                "삭제 완료",
                "카드가 성공적으로 삭제되었습니다."
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("카드 삭제 중 오류 발생: {}", e.getMessage(), e);
            throw e; // GlobalExceptionHandler에서 처리
        }
    }
} 