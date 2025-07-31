package sanghun.project.howtouseai.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import sanghun.project.howtouseai.dto.ApiResponse;
import sanghun.project.howtouseai.dto.ErrorResponse;
import sanghun.project.howtouseai.dto.ResponseHelper;

import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleException(Exception e) {
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