package ru.yandex.practicum.filmorate.service.film;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Service
public interface FilmService {

     Film getFilmById(int filmId);

     List<Film> getAll();

     Film add(Film film);

     Film update(Film film);

     Film addLike(int userId, int filmId);

     void removeLike(int userId, int filmId);

     List<Film> getPopular(int count);
}