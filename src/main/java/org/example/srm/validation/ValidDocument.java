package org.example.srm.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = DocumentValidator.class)
public @interface ValidDocument {
    String message() default "Invalid document number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}