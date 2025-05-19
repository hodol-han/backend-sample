package com.hodol.han.samples.backend.shop.exception;

public class ApiException extends RuntimeException {
  private final ErrorCode errorCode;

  public ApiException(String message, ErrorCode errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
