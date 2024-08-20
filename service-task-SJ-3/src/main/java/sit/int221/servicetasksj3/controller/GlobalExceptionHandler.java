package sit.int221.servicetasksj3.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import sit.int221.servicetasksj3.exceptions.*;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice

public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    // 404 - ItemNotFoundException
    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleItemNotFoundException(ItemNotFoundException exception, WebRequest request) {
        return createErrorResponse(exception.getMessage(), HttpStatus.NOT_FOUND, request);
    }
    // 500 - InternalServerErrorException
    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleAllInternalServerErrorException(InternalServerErrorException exception, WebRequest request) {
        return createErrorResponse(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
    // 400 - ValidationException
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorDetails> handleValidationException(ValidationException exception, WebRequest request) {
        return createErrorResponse("Validation error. Check 'errors' field for details. taskForCreateOrUpdate", HttpStatus.BAD_REQUEST, request, exception.getErrors());
    }
    // 401 - UnauthorizeException
//    @ExceptionHandler(UnauthorizedException.class)
//    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException exception, WebRequest request) {
//        return createErrorResponse(exception.getMessage(), HttpStatus.UNAUTHORIZED, request);
//    }
    // Helper method for creating standard error responses (ErrorResponse)
    private ResponseEntity<ErrorResponse> createErrorResponse(String message, HttpStatus httpStatus, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), message, request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }
    // Helper method for creating validation error responses (ErrorDetails)
    private ResponseEntity<ErrorDetails> createErrorResponse(String message, HttpStatus httpStatus, WebRequest request, List<ErrorDetails.ValidationError> errors) {
        ErrorDetails errorDetails = new ErrorDetails();
            errorDetails.setStatus(httpStatus.value());
            errorDetails.setMessage(message);
            errorDetails.setInstance(request.getDescription(false));
            errorDetails.setErrors(errors);
        return ResponseEntity.status(httpStatus).body(errorDetails);
    }
}