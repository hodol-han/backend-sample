package com.hodol.han.samples.backend.shop.exception;

import java.util.List;

public class ErrorResponse {
  private ErrorCode errorCode;
  private String message;
  private List<String> details;

  public ErrorResponse(ErrorCode errorCode, String message) {
    this.errorCode = errorCode;
    this.message = message;
  }

  public ErrorResponse(ErrorCode errorCode, String message, List<String> details) {
    this.errorCode = errorCode;
    this.message = message;
    this.details = details;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }

  public String getMessage() {
    return message;
  }

  public List<String> getDetails() {
    return details;
  }
}
