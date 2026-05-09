package ru.otus.hw.controllers;

import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import ru.otus.hw.dtos.AuthorDto;
import ru.otus.hw.dtos.BookDto;
import ru.otus.hw.dtos.GenreDto;
import ru.otus.hw.dtos.UserCommentDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;
import ru.otus.hw.services.UserCommentService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
@Slf4j
public class BooksController {

    private final BookService bookService;

    private final GenreService genreService;

    private final AuthorService authorService;

    private final UserCommentService userCommentService;

    @GetMapping("/book")
    public String viewAllBooks(Model model) {
        List<BookDto> books = bookService.findAll();
        model.addAttribute("allBooks", books);
        return "books";
    }

    @GetMapping("/book/{id}")
    public String viewBook(@PathVariable long id, Model model) {
        BookDto book = bookService.findById(id);
        List<UserCommentDto> userComments = userCommentService.findAllByBookId(id);
        model.addAttribute("book", book);
        model.addAttribute("userComments", userComments);
        return "book";
    }

    @GetMapping("/book/edit")
    public String editBook(@PathParam("id") Long id, Model model) {
        BookDto book;
        if (id != null) {
            book = bookService.findById(id);
        } else {
            book = new BookDto();
        }
        model.addAttribute("book", book);
        List<GenreDto> genres = genreService.findAll();
        model.addAttribute("allGenres", genres);

        List<AuthorDto> authors = authorService.findAll();
        model.addAttribute("allAuthors", authors);
        return "book_edit";
    }

    @PostMapping(value = "/book/edit", params = "action=save")
    public String editBook(@ModelAttribute(value = "book") BookDto book,
                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn("Request binding error: {}", bindingResult.getAllErrors());
            return "redirect:/book";
        }
        if (book.id() != null) {
            bookService.update(book.id(),
                    book.title(),
                    book.author().id(),
                    book.genres().stream().map(GenreDto::id).collect(Collectors.toSet()));
            return "redirect:/book/" + book.id();
        } else {
            bookService.insert(book.title(),
                    book.author().id(),
                    book.genres().stream().map(GenreDto::id).collect(Collectors.toSet()));
            return "redirect:/book";
        }
    }

    @PostMapping(value = "/book/edit", params = "action=delete")
    public String deleteBook(@ModelAttribute(value = "book") BookDto book,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn("Request binding error: {}", bindingResult.getAllErrors());
            return "redirect:/book";
        }
        bookService.deleteById(book.id());
        return "redirect:/book";
    }
}
