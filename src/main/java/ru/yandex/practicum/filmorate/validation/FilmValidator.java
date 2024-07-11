package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.time.LocalDate;

public class FilmValidator implements ConstraintValidator<ValidFilm, Film> {

    private static final LocalDate EARLIEST_RELEASE_DATE =
            LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(Film film, ConstraintValidatorContext context) {
        if (film == null) {
            return false;
        }

        if (film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            throw new ValidationException("Incorrect release date");
        }

        return true;
    }
}