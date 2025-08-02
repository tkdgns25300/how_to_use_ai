package sanghun.project.howtouseai.exception;

public class LikeAlreadyExistsException extends RuntimeException {
    
    public LikeAlreadyExistsException(String message) {
        super(message);
    }
    
    public LikeAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
} 