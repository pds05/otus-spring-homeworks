package ru.otus.hw.controllers;

import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.otus.hw.dtos.GenreDto;
import ru.otus.hw.dtos.AuthorDto;
import ru.otus.hw.services.GenreService;
import ru.otus.hw.services.AuthorService;

import java.util.List;

@Controller
@AllArgsConstructor
@Slf4j
public class BooksController {

    private final GenreService genreService;

    private final AuthorService authorService;

    @GetMapping("/book")
    public String viewAllBooks() {
        return "books";
    }

    @GetMapping("/book/{id}")
    public String viewBook(@PathVariable long id) {
        return "book";
    }

    @GetMapping("/book/edit")
    public String editBook(@PathParam("id") Long id, Model model) {
        List<GenreDto> genres = genreService.findAll();
        model.addAttribute("allGenres", genres);

        List<AuthorDto> authors = authorService.findAll();
        model.addAttribute("allAuthors", authors);
        return "book_edit";
    }
}
