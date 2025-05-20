package com.hodol.han.samples.backend.shop.exception;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex) {
    List<String> details =
        ex.getBindingResult().getAllErrors().stream()
            .map(
                error ->
                    error instanceof FieldError fieldError
                        ? fieldError.getField() + ": " + fieldError.getDefaultMessage()
                        : error.getDefaultMessage())
            .collect(Collectors.toList());
    String message = "Invalid input value(s).";
    ErrorResponse errorResponse = new ErrorResponse(ErrorCode.VALIDATION_ERROR, message, details);
    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    Class<?> type = ex.getRequiredType();
    String expectedType = (type != null) ? type.getSimpleName() : "unknown";
    String message =
        String.format(
            "Invalid parameter type: %s. Expected: %s, Provided: %s",
            ex.getName(), expectedType, ex.getValue());
    ErrorResponse errorResponse = new ErrorResponse(ErrorCode.TYPE_MISMATCH, message);
    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex) {
    String message = "Malformed JSON request.";
    ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_JSON, message);
    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
    String code =
        ex.getStatusCode().value() == 404
            ? ErrorCode.NOT_FOUND.name()
            : ex.getStatusCode().toString();
    ErrorCode errorCode;
    try {
      errorCode = ErrorCode.valueOf(code);
    } catch (IllegalArgumentException e) {
      errorCode = ErrorCode.INTERNAL_ERROR; // Fallback to a default ErrorCode
    }
    ErrorResponse errorResponse = new ErrorResponse(errorCode, ex.getReason());
    return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
  }

  @ExceptionHandler(DuplicateUserException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateUserException(DuplicateUserException ex) {
    ErrorResponse errorResponse = new ErrorResponse(ErrorCode.CONFLICT, ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
    ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode(), ex.getMessage());
    if (ex.getErrorCode() == ErrorCode.CONFLICT) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
    ErrorResponse errorResponse =
        new ErrorResponse(ErrorCode.UNAUTHORIZED, "Invalid username or password");
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
    log.error("[INTERNAL_ERROR]", ex);
    String message = "Internal server error occurred.";
    ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INTERNAL_ERROR, message);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}
