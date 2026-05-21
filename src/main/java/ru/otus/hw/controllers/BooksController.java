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
import ru.otus.hw.dtos.BookDto;
import ru.otus.hw.dtos.GenreDto;
import ru.otus.hw.dtos.AuthorDto;
import ru.otus.hw.dtos.BookFromUiDto;
import ru.otus.hw.dtos.UserCommentDto;
import ru.otus.hw.services.BookServiceImpl;
import ru.otus.hw.services.GenreService;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.UserCommentService;

import java.util.List;

@Controller
@AllArgsConstructor
@Slf4j
public class BooksController {

    private final BookServiceImpl bookService;

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

    @PostMapping(value = "/book/edit")
    public String editBook(@ModelAttribute(value = "book") BookFromUiDto book,
                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn("Request binding error: {}", bindingResult.getAllErrors());
            return "redirect:/book";
        }
        var result = bookService.save(book.getId(),
                book.getTitle(),
                book.getAuthorId(),
                book.getGenreIds());
        return "redirect:/book/" + result.id();
    }

    @PostMapping(value = "/book/delete")
    public String deleteBook(Long bookId) {
        bookService.deleteById(bookId);
        return "redirect:/book";
    }
}
