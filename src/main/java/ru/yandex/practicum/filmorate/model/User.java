package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

@Builder
@Data
public class User {
    private int id;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Login cannot be blank")
    private String login;

    private String name;

    @NotNull(message = "Birthday cannot be null")
    @Past(message = "Birthday must be in the past")
    private LocalDate birthday;
}