package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface MpaStorage {
    Collection<Mpa> getAllMpa();

    Mpa getMpaById(int mpaId);

    Mpa getMpaByFilmId(Long id);

    void addMpa(Film film);
}
