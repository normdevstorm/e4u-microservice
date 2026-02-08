package com.renting.authentication_service.config.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class PasswordConstraintValidator implements ConstraintValidator<CustomPasswordValidation,String> {
    @Override
    public void initialize(CustomPasswordValidation constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
//        8 characters length
//        2 letters in Upper Case
//        1 Special Character (!@#$&*)
//        2 numerals (0-9)
//        3 letters in Lower Case
        return s.matches("^(?=.*[A-Z].*[A-Z])(?=.*[!@#$&*])(?=.*[0-9].*[0-9])(?=.*[a-z].*[a-z].*[a-z]).{8}$");
    }
}
