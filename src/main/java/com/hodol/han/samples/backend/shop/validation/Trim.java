package com.hodol.han.samples.backend.shop.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = TrimValidator.class)
@Target({FIELD, METHOD, PARAMETER})
@Retention(RUNTIME)
public @interface Trim {
  String message() default "must not be blank after trimming";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
