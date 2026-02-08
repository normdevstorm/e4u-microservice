package com.renting.authentication_service.config.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CustomCreditCardValidator implements ConstraintValidator<CustomCreditCardValidation, String> {

    @Override
    public void initialize(CustomCreditCardValidation constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s.matches("^4[0-9]{12}(?:[0-9]{3})?$");
    }
}
