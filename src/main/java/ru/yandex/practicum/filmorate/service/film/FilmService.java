package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
     void addLike(Long userId, Long filmId);

     void removeLike(Long userId, Long filmId);

     List<Film> getPopular(int count);

     List<Film> getAll();

     Film getById(Long id);

     Film add(Film film);

     Film update(Film film);
}
