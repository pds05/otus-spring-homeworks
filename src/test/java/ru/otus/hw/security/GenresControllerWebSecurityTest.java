package ru.otus.hw.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controllers.GenresController;
import ru.otus.hw.services.GenreService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GenresController.class)
@Import(SecurityConfig.class)
public class GenresControllerWebSecurityTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private GenreService genreService;

    @Test
    @WithMockUser(username = "tester", password = "pwd123", roles = "USER")
    public void testGenrePageSignedIn() throws Exception {
        mockMvc.perform(get("/genre"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGenrePageNotSignedIn() throws Exception {
        mockMvc.perform(get("/genre"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}
