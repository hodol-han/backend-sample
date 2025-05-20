package com.hodol.han.samples.backend.shop.validation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TrimValidatorTest {
  private final TrimValidator validator = new TrimValidator();

  @Test
  void validNullShouldBeValid() {
    assertTrue(validator.isValid(null, null));
  }

  @Test
  void validNonEmptyShouldBeValid() {
    assertTrue(validator.isValid("abc", null));
    assertTrue(validator.isValid(" abc ", null));
  }

  @Test
  void whitespaceOnlyShouldBeInvalid() {
    assertFalse(validator.isValid("   ", null));
    assertFalse(validator.isValid("", null));
  }
}
