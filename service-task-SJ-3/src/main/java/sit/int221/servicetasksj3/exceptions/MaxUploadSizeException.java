package sit.int221.servicetasksj3.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class MaxUploadSizeException extends RuntimeException {
    public MaxUploadSizeException(String message) {
        super(message);
    }
}