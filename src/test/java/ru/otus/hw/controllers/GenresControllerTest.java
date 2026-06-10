package ru.otus.hw.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dtos.GenreDto;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GenresController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class GenresControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private GenreService genreService;

    private final List<GenreDto> genres = List.of(new GenreDto(1L, "Genre 1"),
            new GenreDto(2L, "Genre 2"),
            new GenreDto(3L, "Genre 3"));

    @Test
    void shouldRenderGenresPageWithCorrectViewAndModel() throws Exception {
        when(genreService.findAll()).thenReturn(genres);
        mvc.perform(get("/genre"))
                .andExpect(status().isOk())
                .andExpect(view().name("genres"))
                .andExpect(model().attributeExists("allGenres"));
    }
}
