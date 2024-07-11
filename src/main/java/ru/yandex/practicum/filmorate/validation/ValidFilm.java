package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = FilmValidator.class)
@Documented
public @interface ValidFilm {
    String message() default "{Film.invalid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}