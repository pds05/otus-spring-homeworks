package ru.otus.hw.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.otus.hw.controllers.BooksController;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookServiceImpl;
import ru.otus.hw.services.GenreService;
import ru.otus.hw.services.UserCommentService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BooksController.class)
@Import(SecurityConfig.class)
public class BooksControllerWebSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookServiceImpl bookService;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private UserCommentService userCommentService;

    @Test
    @WithMockUser(username = "tester", password = "pwd123", roles = "USER")
    public void testBookPageSignedIn() throws Exception {
        mockMvc.perform(get("/book"))
                .andExpect(status().isOk());
    }

    @Test
    public void testBookPageNotSignedIn() throws Exception {
        mockMvc.perform(get("/book"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "tester", password = "pwd123", roles = "USER")
    public void testBookInfoPageSignedIn() throws Exception {
        mockMvc.perform(get("/book/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testBookInfoPageNotSignedIn() throws Exception {
        mockMvc.perform(get("/book/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "tester", password = "pwd123", roles = "USER")
    public void testBookEditPageSignedIn() throws Exception {
        mockMvc.perform(get("/book/edit"))
                .andExpect(status().isOk());
    }

    @Test
    public void testBookEditPageNotSignedIn() throws Exception {
        mockMvc.perform(get("/book/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

}
