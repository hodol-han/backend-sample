package com.hodol.han.samples.backend.shop.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TrimValidator implements ConstraintValidator<Trim, String> {
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) return true;
    return !value.trim().isEmpty();
  }
}
