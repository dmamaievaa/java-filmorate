package ru.yandex.practicum.filmorate.service.film;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Service
public interface FilmService {

     void addLike(Long userId, Long filmId);

     void removeLike(Long userId, Long filmId);

     List<Film> getPopular(Long count);
}