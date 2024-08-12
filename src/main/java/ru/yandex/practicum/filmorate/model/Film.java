package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
@Data
public class Film {
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Size(max = 200, message = "Description cannot exceed 200 characters")
    private String description;

    @NotNull(message = "Release date cannot be null")
    @PastOrPresent(message = "Release date cannot be in the future")
    private LocalDate releaseDate;

    @Positive(message = "Duration must be positive")
    private long duration;

    private Set<Long> likes;

    @Builder.Default
    private List<FilmGenre> filmGenre = new ArrayList<>();

    @Builder
    public Film(Long id, String name, String description, LocalDate releaseDate, long duration, Set<Long> likes,
                List<FilmGenre> filmGenre) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likes = likes != null ? likes : new HashSet<>();
        this.filmGenre = filmGenre;
    }
}