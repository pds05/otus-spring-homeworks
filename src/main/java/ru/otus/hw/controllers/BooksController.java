package ru.otus.hw.controllers;

import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;
import ru.otus.hw.dtos.AuthorDto;
import ru.otus.hw.dtos.GenreDto;
import ru.otus.hw.services.AuthorServiceReactive;
import ru.otus.hw.services.GenreServiceReactive;

import java.util.List;

@Controller
@AllArgsConstructor
@Slf4j
public class BooksController {

    private final GenreServiceReactive genreServiceReactive;

    private final AuthorServiceReactive authorServiceReactive;

    @GetMapping("/book")
    public String viewAllBooks() {
        return "books";
    }

    @GetMapping("/book/{id}")
    public String viewBook(@PathVariable String id) {
        return "book";
    }

    @GetMapping("/book/edit")
    public Mono<String> editBook(@PathParam("id") String id, Model model) {
        Mono<List<AuthorDto>> authorListMono = authorServiceReactive.findAll().collectList();
        Mono<List<GenreDto>> genreListMono = genreServiceReactive.findAll().collectList();

        return Mono.zip(authorListMono, genreListMono).map(tuple2 -> {
            List<AuthorDto> authorList = tuple2.getT1();
            List<GenreDto> genreList = tuple2.getT2();
            model.addAttribute("allAuthors", authorList);
            model.addAttribute("allGenres", genreList);
            return "book_edit";
        });
    }
}
