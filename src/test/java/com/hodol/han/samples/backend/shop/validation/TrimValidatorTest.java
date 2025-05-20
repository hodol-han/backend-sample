package com.hodol.han.samples.backend.shop.validation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TrimValidatorTest {
  private final TrimValidator validator = new TrimValidator();

  @Test
  @DisplayName("should be valid when value is null")
  void testValidNullShouldBeValid() {
    assertTrue(validator.isValid(null, null));
  }

  @Test
  @DisplayName("should be valid when value is non-empty")
  void testValidNonEmptyShouldBeValid() {
    assertTrue(validator.isValid("abc", null));
    assertTrue(validator.isValid(" abc ", null));
  }

  @Test
  @DisplayName("should be invalid when value is whitespace only")
  void testWhitespaceOnlyShouldBeInvalid() {
    assertFalse(validator.isValid("   ", null));
    assertFalse(validator.isValid("", null));
  }
}
