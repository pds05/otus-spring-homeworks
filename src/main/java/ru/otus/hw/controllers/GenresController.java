package ru.otus.hw.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dtos.GenreDto;
import ru.otus.hw.services.GenreServiceReactive;

@Controller
@AllArgsConstructor
public class GenresController {

    private final GenreServiceReactive genreServiceReactive;

    @GetMapping("/genre")
    public Mono<String> viewAllGenres(Model model) {
        Flux<GenreDto> genreDtoFlux = genreServiceReactive.findAll();
        return genreDtoFlux.collectList()
                .map(genreList -> {
                    model.addAttribute("allGenres", genreList);
                    return "genres";
                });
    }
}
