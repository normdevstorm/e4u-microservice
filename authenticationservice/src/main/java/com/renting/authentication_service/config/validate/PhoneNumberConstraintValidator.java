package com.renting.authentication_service.config.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberConstraintValidator implements ConstraintValidator<CustomPhoneNumValidation, String  > {
    @Override
    public void initialize(CustomPhoneNumValidation constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s.matches("^\\+84-\\d{2}-\\d{3}-\\d{4}$");
    }
}
