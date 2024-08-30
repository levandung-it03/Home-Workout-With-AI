package com.restproject.backend.annotations.constraint;

import com.restproject.backend.annotations.validator.LevelEnumValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = { LevelEnumValidator.class })
@Target({ FIELD })
@Retention(RUNTIME)
public @interface LevelEnumConstraint {
    String message() default "Invalid level";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
