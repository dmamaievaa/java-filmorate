package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Builder
@Data
public class Film {
    private int id;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Size(max = 200, message = "Description cannot exceed 200 characters")
    private String description;

    @NotNull(message = "Release date cannot be null")
    @PastOrPresent(message = "Release date cannot be in the future")
    private LocalDate releaseDate;

    @Positive(message = "Duration must be positive")
    private long duration;

    @EqualsAndHashCode.Exclude
    @JsonIgnore
    @Builder.Default
    private Set<Integer> likes = new HashSet<>();
}