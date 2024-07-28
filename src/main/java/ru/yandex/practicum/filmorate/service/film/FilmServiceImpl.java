package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public void addLike(Long id, Long userId) {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            throw new NotFoundException("Film not found");
        }

        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        film.getLikes().add(user.getId());
        log.trace("User {} liked the film {}", user.getName(), film.getName());
    }


    public void removeLike(Long id, Long userId) {
        Film film = filmStorage.getFilmById(id);
        User user = userStorage.getUserById(userId);
        film.getLikes().remove(user.getId());
        log.trace("User {} removed like from the film {}", user.getName(), film.getName());
    }

    @Override
    public List<Film> getPopular(Long count) {
        return filmStorage.getAll().stream()
                .sorted((o1, o2) -> Long.compare(o2.getLikes().size(), o1.getLikes().size()))
                .limit(count)
                .toList();
    }
}
