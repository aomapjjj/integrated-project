package sit.int221.servicetasksj3.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // จะแสดง HTTP Status 409
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}