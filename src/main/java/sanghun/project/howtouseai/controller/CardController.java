package sanghun.project.howtouseai.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sanghun.project.howtouseai.dto.ApiResponse;
import sanghun.project.howtouseai.dto.CardCreateRequest;
import sanghun.project.howtouseai.dto.CardResponse;
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
} 