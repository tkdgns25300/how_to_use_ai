package sanghun.project.howtouseai.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import sanghun.project.howtouseai.dto.ApiResponse;
import sanghun.project.howtouseai.dto.ErrorResponse;

import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleException(Exception e) {
        // favicon.ico 관련 에러는 무시
        if (e instanceof NoResourceFoundException && 
            e.getMessage() != null && 
            e.getMessage().contains("favicon.ico")) {
            log.debug("favicon.ico 요청 무시: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
        
        // 루트 경로 관련 에러는 무시
        if (e instanceof NoResourceFoundException && 
            e.getMessage() != null && 
            e.getMessage().contains("No static resource .")) {
            log.debug("루트 경로 요청 무시: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
        
        log.error("Unexpected error occurred: ", e);
        
        ErrorResponse errorData = ErrorResponse.builder()
                .errorCode("INTERNAL_SERVER_ERROR")
                .errorMessage("서버 내부 오류가 발생했습니다.")
                .details(e.getMessage())
                .build();
        
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>builder()
                .success(false)
                .data(errorData)
                .message("요청 처리 중 오류가 발생했습니다.")
                .build();
        
        return ResponseEntity.internalServerError().body(response);
    }
    
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleIOException(IOException e) {
        log.error("파일 입출력 오류 발생: ", e);
        
        ErrorResponse errorData = ErrorResponse.builder()
                .errorCode("FILE_IO_ERROR")
                .errorMessage("파일 처리 중 오류가 발생했습니다.")
                .details(e.getMessage())
                .build();
        
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>builder()
                .success(false)
                .data(errorData)
                .message("파일 업로드에 실패했습니다.")
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleCardNotFound(
            CardNotFoundException e) {
        
        log.warn("카드 없음 예외 발생: {}", e.getMessage());
        
        ErrorResponse errorData = ErrorResponse.builder()
                .errorCode("CARD_NOT_FOUND")
                .errorMessage(e.getMessage())
                .details("Card not found in database")
                .build();
        
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>builder()
                .success(false)
                .data(errorData)
                .message("카드를 찾을 수 없습니다.")
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleUnauthorizedAccess(
            UnauthorizedAccessException e) {
        
        log.warn("권한 없음 예외 발생: {}", e.getMessage());
        
        ErrorResponse errorData = ErrorResponse.builder()
                .errorCode("UNAUTHORIZED_ACCESS")
                .errorMessage(e.getMessage())
                .details("User does not have permission to access this resource")
                .build();
        
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>builder()
                .success(false)
                .data(errorData)
                .message("접근 권한이 없습니다.")
                .build();
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
    
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleCategoryNotFound(
            CategoryNotFoundException e) {
        
        log.warn("카테고리 없음 예외 발생: {}", e.getMessage());
        
        ErrorResponse errorData = ErrorResponse.builder()
                .errorCode("CATEGORY_NOT_FOUND")
                .errorMessage(e.getMessage())
                .details("Category not found in database")
                .build();
        
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>builder()
                .success(false)
                .data(errorData)
                .message("카테고리를 찾을 수 없습니다.")
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    @ExceptionHandler(CardAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleCardAlreadyExists(
            CardAlreadyExistsException e) {
        
        log.warn("카드 중복 예외 발생: {}", e.getMessage());
        
        ErrorResponse errorData = ErrorResponse.builder()
                .errorCode("DUPLICATE_CARD")
                .errorMessage(e.getMessage())
                .details("Card title already exists for this user")
                .build();
        
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>builder()
                .success(false)
                .data(errorData)
                .message("카드 생성에 실패했습니다.")
                .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    
    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleCategoryAlreadyExists(
            CategoryAlreadyExistsException e) {
        
        log.warn("카테고리 중복 예외 발생: {}", e.getMessage());
        
        ErrorResponse errorData = ErrorResponse.builder()
                .errorCode("DUPLICATE_CATEGORY")
                .errorMessage(e.getMessage())
                .details("Category name already exists in database")
                .build();
        
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>builder()
                .success(false)
                .data(errorData)
                .message("카테고리 생성에 실패했습니다.")
                .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    
    @ExceptionHandler(LikeAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleLikeAlreadyExists(
            LikeAlreadyExistsException e) {
        
        log.warn("좋아요 중복 예외 발생: {}", e.getMessage());
        
        ErrorResponse errorData = ErrorResponse.builder()
                .errorCode("LIKE_ALREADY_EXISTS")
                .errorMessage(e.getMessage())
                .details("User already liked this card")
                .build();
        
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>builder()
                .success(false)
                .data(errorData)
                .message("이미 좋아요한 카드입니다.")
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleIllegalArgumentException(
            IllegalArgumentException e) {
        
        log.warn("잘못된 인자 예외 발생: {}", e.getMessage());
        
        ErrorResponse errorData = ErrorResponse.builder()
                .errorCode("INVALID_ARGUMENT")
                .errorMessage(e.getMessage())
                .details("Invalid argument provided")
                .build();
        
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>builder()
                .success(false)
                .data(errorData)
                .message("잘못된 요청입니다.")
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleValidationError(
            MethodArgumentNotValidException e) {
        
        String details = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        log.warn("Validation error: {}", details);
        
        ErrorResponse errorData = ErrorResponse.builder()
                .errorCode("VALIDATION_ERROR")
                .errorMessage("입력 데이터가 올바르지 않습니다.")
                .details(details)
                .build();
        
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>builder()
                .success(false)
                .data(errorData)
                .message("입력 검증에 실패했습니다.")
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }
} 