package ru.otus.hw.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.otus.hw.dtos.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.List;

@Controller
@AllArgsConstructor
public class AuthorsController {

    private final AuthorService authorService;

    @GetMapping(path = "/author")
    public String viewAllAuthors(Model model) {
        List<AuthorDto> authors = authorService.findAll();
        model.addAttribute("allAuthors", authors);
        return "authors";
    }
}
