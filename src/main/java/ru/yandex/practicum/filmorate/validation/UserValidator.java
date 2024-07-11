package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class UserValidator implements ConstraintValidator<ValidUser, User> {

    @Override
    public boolean isValid(User user, ConstraintValidatorContext context) {
        if (user == null) {
            return false;
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Incorrect birthday date");
        }

        return true;
    }
}