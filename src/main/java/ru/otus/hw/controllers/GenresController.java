package ru.otus.hw.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.otus.hw.dtos.GenreDto;
import ru.otus.hw.services.GenreService;

import java.util.List;

@Controller
@AllArgsConstructor
public class GenresController {

    private final GenreService genreService;

    @GetMapping("/genre")
    public String viewAllGenres(Model model) {
        List<GenreDto> genres = genreService.findAll();
        model.addAttribute("allGenres", genres);
        return "genres";
    }
}
