package com.hodol.han.samples.backend.shop.exception;

public enum ErrorCode {
  VALIDATION_ERROR,
  TYPE_MISMATCH,
  INVALID_JSON,
  NOT_FOUND,
  INTERNAL_ERROR,
  CONFLICT,
  UNAUTHORIZED;

  @Override
  public String toString() {
    return name();
  }
}
