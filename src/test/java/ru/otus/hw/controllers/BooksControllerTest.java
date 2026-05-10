package ru.otus.hw.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.otus.hw.dtos.AuthorDto;
import ru.otus.hw.dtos.BookDto;
import ru.otus.hw.dtos.GenreDto;
import ru.otus.hw.dtos.UserCommentDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;
import ru.otus.hw.services.UserCommentService;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = BooksController.class)
public class BooksControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private BookService bookService;

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
                .andExpect(view().name("books"))
                .andExpect(model().attributeExists("allBooks"));
    }

    @Test
    void shouldRenderBookPageWithCorrectViewAndModel() throws Exception {
        when(bookService.findById(1L)).thenReturn(books.get(0));
        when(userCommentService.findAllByBookId(1L)).thenReturn(comments);
        mvc.perform(get("/book/1")).andExpect(status().isOk())
                .andExpect(view().name("book"))
                .andExpect(model().attributeExists("book", "userComments"));
    }

    @Test
    void shouldRenderEditPageWithCorrectViewAndModel() throws Exception {
        when(bookService.findById(1L)).thenReturn(books.get(0));
        mvc.perform(get("/book/edit").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("book_edit"))
                .andExpect(model().attributeExists("book", "allAuthors", "allGenres"));
    }

    @Test
    void shouldUpdateBookAndRedirectToBookPage() throws Exception {
        when(bookService.findById(1L)).thenReturn(books.get(0));
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("id", "1");
        form.add("title", "new Book 1");
        form.add("author.id", "2");
        form.add("genres", "GenreDto[id=3, name=Genre_3]");
        form.add("genres", "GenreDto[id=4, name=Genre_4]");
        form.add("action", "save");
        mvc.perform(post("/book/edit").formFields(form))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/book/1"));
        verify(bookService, times(1)).update(1L, "new Book 1", 2L, Set.of(3L, 4L));
    }

    @Test
    void shouldAddBookAndRedirectToBooksPage() throws Exception {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("title", "new Book");
        form.add("author.id", "1");
        form.add("genres", "GenreDto[id=1, name=Genre_1]");
        form.add("genres", "GenreDto[id=2, name=Genre_2]");
        form.add("action", "save");
        mvc.perform(post("/book/edit").formFields(form))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/book"));
        verify(bookService, times(1)).insert("new Book", 1L, Set.of(1L, 2L));
    }

    @Test
    void shouldDeleteBookAndRedirectToBooksPage() throws Exception {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("id", "1");
        form.add("action", "delete");
        mvc.perform(post("/book/edit").formFields(form))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/book"));
        verify(bookService, times(1)).deleteById(1L);
    }
}
