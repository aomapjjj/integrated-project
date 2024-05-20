package sit.int221.servicetasksj3.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {
    private final List<ErrorDetails.ValidationError> errors = new ArrayList<>();

    public ValidationException(String message) {
        super(message);
    }

    public void addValidationError(String field, String message) {
        this.errors.add(new ErrorDetails.ValidationError(field, message));
    }

    public List<ErrorDetails.ValidationError> getErrors() {
        return errors;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
