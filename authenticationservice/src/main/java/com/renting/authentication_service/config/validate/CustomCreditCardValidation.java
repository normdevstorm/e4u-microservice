package com.renting.authentication_service.config.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CustomCreditCardValidator.class )
@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CustomCreditCardValidation {
    String message() default "Invalid credit card number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
