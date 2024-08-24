package ru.yandex.practicum.filmorate.service.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {

    private final MpaStorage mpaDbStorage;

    @Override
    public List<Mpa> getAllMpa() {
        return mpaDbStorage.getAllMpa();
    }

    @Override
    public Mpa getMpaById(int id) {
        return mpaDbStorage.getMpaById(id);
    }
}
