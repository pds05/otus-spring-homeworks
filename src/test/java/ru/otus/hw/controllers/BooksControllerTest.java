package ru.otus.hw.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dtos.AuthorDto;
import ru.otus.hw.dtos.BookDto;
import ru.otus.hw.dtos.GenreDto;
import ru.otus.hw.dtos.UserCommentDto;
import ru.otus.hw.services.*;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = BooksController.class)
public class BooksControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private BookServiceImpl bookService;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private UserCommentService userCommentService;

    private final List<BookDto> books = List.of(new BookDto(1L, "Book 1", new AuthorDto(1L, "Author 1"), List.of(new GenreDto(1L, "Genre 1"), new GenreDto(2L, "Genre 2"))));

    private final List<UserCommentDto> comments = List.of(new UserCommentDto(1L, "Comment 1 book 1", 1L), new UserCommentDto(2L, "Comment 2 book 1", 1L));

    @Test
    void shouldRenderBooksPageWithCorrectViewAndModel() throws Exception {
        when(bookService.findAll()).thenReturn(books);
        mvc.perform(get("/book"))
                .andExpect(status().isOk())
                .andExpect(view().name("books"));
    }

    @Test
    void shouldRenderBookPageWithCorrectViewAndModel() throws Exception {
        when(bookService.findById(1L)).thenReturn(books.get(0));
        when(userCommentService.findAllByBookId(1L)).thenReturn(comments);
        mvc.perform(get("/book/1")).andExpect(status().isOk())
                .andExpect(view().name("book"));
    }

    @Test
    void shouldRenderEditPageWithCorrectViewAndModel() throws Exception {
        when(bookService.findById(1L)).thenReturn(books.get(0));
        mvc.perform(get("/book/edit").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("book_edit"))
                .andExpect(model().attributeExists( "allAuthors", "allGenres"));
    }
}
