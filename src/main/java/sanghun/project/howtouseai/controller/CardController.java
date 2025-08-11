package sanghun.project.howtouseai.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CardResponse>>> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("카드 목록 조회 API 호출: page={}, size={}", page, size);
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<CardResponse> cards = cardService.getAllCards(pageable);
            
            ApiResponse<Page<CardResponse>> response = ResponseHelper.success(
                cards,
                "카드 목록을 성공적으로 조회했습니다."
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("카드 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            throw e; // GlobalExceptionHandler에서 처리
        }
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<ApiResponse<CardResponse>> getCardById(
            @PathVariable Long cardId,
            @RequestParam(name = "uuid", required = false) String userUuid) {
        log.info("카드 상세 조회 API 호출: cardId={}, userUuid={}", cardId, userUuid);
        
        try {
            CardResponse cardResponse = cardService.getCardById(cardId, userUuid);
            
            ApiResponse<CardResponse> response = ResponseHelper.success(
                cardResponse,
                "카드 상세 정보를 성공적으로 조회했습니다."
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("카드 상세 조회 중 오류 발생: {}", e.getMessage(), e);
            throw e; // GlobalExceptionHandler에서 처리
        }
    }

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
            @RequestBody java.util.Map<String, String> request) {
        
        String uuid = request.get("uuid");
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