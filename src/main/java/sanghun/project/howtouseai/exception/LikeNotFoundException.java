package sanghun.project.howtouseai.exception;

public class LikeNotFoundException extends RuntimeException {
    
    public LikeNotFoundException(String message) {
        super(message);
    }
    
    public LikeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 