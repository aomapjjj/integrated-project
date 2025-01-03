package sit.int221.servicetasksj3.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import sit.int221.servicetasksj3.dtos.filesDTO.AttachmentDTO;
import sit.int221.servicetasksj3.dtos.filesDTO.AttachmentResponseDTO;
import sit.int221.servicetasksj3.exceptions.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @Value("${file.max-file-size}")
    private int MAX_FILE_SIZE_MB;

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
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException exception, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Username or password is incorrect", request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException exception, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Username or password is incorrect", request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    // 403 - ForbiddenException
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException exception, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), exception.getMessage(), request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException exception, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Resource not found", request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorDetails> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST.value(), "Validation error. Check 'errors' field for details.", request.getDescription(false));
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errorDetails.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errorDetails);
    }
    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorDetails> handleHandlerMethodValidationException (HandlerMethodValidationException exception, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST.value(), "Validation error. Check 'errors' field for details.", request.getDescription(false));
        for (ParameterValidationResult param : exception.getAllValidationResults()) {
            errorDetails.addValidationError(param.getMethodParameter().getParameterName(), param.getResolvableErrors().get(0).getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }
    // 400 - HttpMessageNotReadableException: Bad Request
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDetails> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception, WebRequest request) {
        List<ErrorDetails.ValidationError> errors = extractValidationErrors(exception);
        return createErrorResponse("Request body contains invalid data. Please check the 'errors' field for details.", HttpStatus.BAD_REQUEST, request, errors);
    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorDetails> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException exception, WebRequest request) {
        List<ErrorDetails.ValidationError> errors = List.of(
                    new ErrorDetails.ValidationError("files","Some files were not uploaded due to size limits. Ensure file size does not exceed " + MAX_FILE_SIZE_MB + " MB."
                ));
        return createErrorResponse("Validation error. Check 'errors' field for details.", HttpStatus.BAD_REQUEST, request, errors);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException exception, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Access is denied", request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
    private List<ErrorDetails.ValidationError> extractValidationErrors(HttpMessageNotReadableException exception) {
        Throwable cause = exception.getCause();
        if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException) {
            com.fasterxml.jackson.databind.exc.InvalidFormatException invalidFormatException =
                    (com.fasterxml.jackson.databind.exc.InvalidFormatException) cause;
            String fieldName = invalidFormatException.getPath().stream()
                    .map(ref -> ref.getFieldName())
                    .collect(Collectors.joining("."));
            String errorMessage = String.format("Cannot deserialize value of type '%s' from String '%s': not one of the accepted values",
                    invalidFormatException.getTargetType().getSimpleName(),
                    invalidFormatException.getValue());
            return List.of(new ErrorDetails.ValidationError(fieldName, errorMessage));
        }
        return List.of(new ErrorDetails.ValidationError("message", cause != null ? cause.getMessage() : "Unknown error"));
    }
    private ResponseEntity<ErrorResponse> createErrorResponse(String message, HttpStatus httpStatus, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), message, request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }
    private ResponseEntity<ErrorDetails> createErrorResponse(String message, HttpStatus httpStatus, WebRequest request, List<ErrorDetails.ValidationError> errors) {
        ErrorDetails errorDetails = new ErrorDetails();
            errorDetails.setStatus(httpStatus.value());
            errorDetails.setMessage(message);
            errorDetails.setInstance(request.getDescription(false));
            errorDetails.setErrors(errors);
        return ResponseEntity.status(httpStatus).body(errorDetails);
    }
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<?> handleConflictException(ConflictException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage(), request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
}