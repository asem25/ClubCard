package ru.semavin.ClubCard.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ValidationUtils {
    private final Validator validator;
    @Autowired
    public ValidationUtils(Validator validator) {
        this.validator = validator;
    }
    public <T> void validate(T object) throws ConstraintViolationException{
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
