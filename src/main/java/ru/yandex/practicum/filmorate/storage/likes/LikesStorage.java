package ru.yandex.practicum.filmorate.storage.likes;

import java.util.Set;

public interface LikesStorage {
    Set<Long> getLikesByFilmId(Long filmId);
}
