package com.taskdock.taskdock_api.exceptions;

import io.jsonwebtoken.JwtException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex) {
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
    log.error(apiError.toString(), ex);
    return ResponseEntity.status(apiError.status()).body(apiError);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex) {
    ApiError apiError =
        new ApiError(HttpStatus.NOT_FOUND, ex.getResourceName() + ex.getResourceId());
    log.error(apiError.toString(), ex);
    return ResponseEntity.status(apiError.status()).body(apiError);
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<ApiError> handleUsernameNotFound(UsernameNotFoundException ex) {
    ApiError apiError =
        new ApiError(HttpStatus.NOT_FOUND, "Username not found with username: " + ex.getMessage());
    log.error(apiError.toString(), ex);
    return ResponseEntity.status(apiError.status()).body(apiError);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ApiError> handleAuthentication(AuthenticationException ex) {
    ApiError apiError =
        new ApiError(HttpStatus.UNAUTHORIZED, "Authentication failed:" + ex.getMessage());
    log.error(apiError.toString(), ex);
    return ResponseEntity.status(apiError.status()).body(apiError);
  }

  @ExceptionHandler(JwtException.class)
  public ResponseEntity<ApiError> handleJwt(JwtException ex) {
    ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, "Invalid JWT: " + ex.getMessage());
    log.error(apiError.toString(), ex);
    return ResponseEntity.status(apiError.status()).body(apiError);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {
    ApiError apiError =
        new ApiError(HttpStatus.FORBIDDEN, "Access Denied: Insufficient Permissions");
    log.error(apiError.toString(), ex);
    return ResponseEntity.status(apiError.status()).body(apiError);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleResourceNotFound(MethodArgumentNotValidException ex) {
    List<ApiFieldError> errorList =
        ex.getBindingResult().getFieldErrors().stream()
            .map(error -> new ApiFieldError(error.getField(), error.getDefaultMessage()))
            .toList();

    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Input Validation Failed", errorList);

    log.error(apiError.toString(), ex);
    return ResponseEntity.status(apiError.status()).body(apiError);
  }
}
