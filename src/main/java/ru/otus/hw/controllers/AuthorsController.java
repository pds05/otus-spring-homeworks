package ru.otus.hw.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dtos.AuthorDto;
import ru.otus.hw.services.AuthorServiceReactive;

@Controller
@AllArgsConstructor
public class AuthorsController {

    private final AuthorServiceReactive authorServiceReactive;

    @GetMapping(path = "/author")
    public Mono<String> viewAllAuthors(Model model) {
        Flux<AuthorDto> authorDtoFlux = authorServiceReactive.findAll();
        return authorDtoFlux.collectList().map(authorList -> {
            model.addAttribute("allAuthors", authorList);
            return "authors";
        });
    }
}
