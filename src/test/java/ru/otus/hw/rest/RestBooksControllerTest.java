package ru.otus.hw.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.otus.hw.dtos.*;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookServiceImpl;
import ru.otus.hw.services.GenreService;
import ru.otus.hw.services.UserCommentService;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RestBooksController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class RestBooksControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    void shouldReturnBooks() throws Exception {
        when(bookService.findAll()).thenReturn(books);

        mvc.perform(get("/api/book")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(books)));
    }

    @Test
    void shouldReturnBookById() throws Exception {
        when(bookService.findById(1L)).thenReturn(books.get(0));

        mvc.perform(get("/api/book/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(books.get(0))));
    }

    @Test
    void shouldReturnUserCommentsByBookId() throws Exception {
        when(userCommentService.findAllByBookId(1L)).thenReturn(comments);

        mvc.perform(get("/api/book/{id}/user_comment", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(comments)));
    }

    @Test
    void shouldAddBook() throws Exception {
        BookDto resp = new BookDto(1L, "new Book",
                new AuthorDto(2L, "Author name"),
                List.of(new GenreDto(2L, "Genre 2"), new GenreDto(3L, "Genre 3")));

        BookFromUiDto book = new BookFromUiDto(null, "new Book", 2L, Set.of("2", "3"));

        when(bookService.insert(anyString(), anyLong(), anySet()))
                .thenReturn(resp);

        mvc.perform(post("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L));
        verify(bookService, times(1)).insert("new Book", 2L, Set.of(2L, 3L));
    }

    @Test
    void shouldUpdateBook() throws Exception {
        BookDto resp = new BookDto(1L, "edited Book",
                new AuthorDto(2L, "Author name"),
                List.of(new GenreDto(2L, "Genre 2"), new GenreDto(3L, "Genre 3")));

        BookFromUiDto book = new BookFromUiDto(null, "new Book", 2L, Set.of("2", "3"));

        when(bookService.update(anyLong(), anyString(), anyLong(), anySet()))
                .thenReturn(resp);

        mvc.perform(put("/api/book/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L));
        verify(bookService, times(1)).update(1L, "new Book", 2L, Set.of(2L, 3L));
    }

    @Test
    void shouldDeleteBookById() throws Exception {
        mvc.perform(delete("/api/book/{id}", 1))
                .andExpect(status().isNoContent());
        verify(bookService, times(1)).deleteById(1L);
    }
}
