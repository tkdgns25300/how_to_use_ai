package sanghun.project.howtouseai.dto;

public class ResponseHelper {
    
    // 성공 응답 생성
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .build();
    }
    
    // 성공 응답 생성 (기본 메시지)
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "요청이 성공적으로 처리되었습니다.");
    }
    
    // 에러 응답 생성
    public static ApiResponse<ErrorResponse> error(String errorCode, String message, String details) {
        ErrorResponse errorData = ErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(message)
                .details(details)
                .build();
        
        ApiResponse<ErrorResponse> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setData(errorData);
        response.setMessage("요청 처리 중 오류가 발생했습니다.");
        return response;
    }
    
    // 에러 응답 생성 (상세 정보 없음)
    public static ApiResponse<ErrorResponse> error(String errorCode, String message) {
        return error(errorCode, message, null);
    }
} 