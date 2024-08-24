package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.mpa.MpaService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MPAController {

    private final MpaService mpaService;


    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable("id") int mpaId) {
        return mpaService.getMpaById(mpaId);
    }

    @GetMapping()
    public List<Mpa> getAllMpa() {
        return mpaService.getAllMpa();
    }
}