package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MpaDbStorageTest {

    private final MpaDbStorage mpaDbStorage;
    private Film film;

    @BeforeEach
    void setUp() {
        film = TestUtil.createFilm("Gone with the wind", LocalDate.of(1939, 8, 17), 136, 1, "G");
    }

    @Test
    void shouldGetAllMpa() {
        Collection<Mpa> mpa = mpaDbStorage.getAllMpa();
        assertEquals(5, mpa.size());
    }

    @Test
    void shouldGetMpaById() {
        Mpa mpaTest = mpaDbStorage.getMpaById(2);
        assertEquals("PG", mpaTest.getName());
    }

    @Test
    void shouldAddMpaToFilm() {
        film.setMpa(Mpa.builder()
                .id(1)
                .name("PG-13")
                .build());
        mpaDbStorage.addMpa(film);
        assertNotNull(film.getMpa());
    }

    @Test
    void shouldThrowValidationExceptionForInvalidMpaId() {
        film.setMpa(Mpa.builder()
                .id(10)
                .build());

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            mpaDbStorage.addMpa(film);
        });

        assertEquals("MPA with ID 10 not found", exception.getMessage());
    }
}