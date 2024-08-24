package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    private Long id;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Login cannot be blank")
    private String login;

    private String name;

    @NotNull(message = "Birthday cannot be null")
    @Past(message = "Birthday must be in the past")
    private LocalDate birthday;
    @Builder.Default
    private Set<Long> friends = new HashSet<>();

    public User(Long id,
                String email,
                String login,
                String name,
                LocalDate birthday,
                Set<Long> friends) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = (name == null || name.isBlank()) ? login : name;
        this.birthday = birthday;
        this.friends = (friends == null) ? new HashSet<>() : new HashSet<>(friends);
    }
}