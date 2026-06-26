package org.example.srm.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = FutureDateValidator.class)
public @interface ValidFutureDate {
    String message() default "Date must be at least 1 day in the future";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}