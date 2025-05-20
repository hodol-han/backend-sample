package com.hodol.han.samples.backend.shop.exception;

public class DuplicateUserException extends ApiException {
  public DuplicateUserException(String username) {
    super("Duplicate username: " + username, ErrorCode.CONFLICT);
  }
}
