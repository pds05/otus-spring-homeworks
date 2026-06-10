package ru.otus.hw.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.otus.hw.controllers.AuthorsController;
import ru.otus.hw.controllers.BooksController;
import ru.otus.hw.controllers.GenresController;
import ru.otus.hw.dtos.AuthorDto;
import ru.otus.hw.dtos.BookDto;
import ru.otus.hw.dtos.BookFromUiDto;
import ru.otus.hw.dtos.GenreDto;
import ru.otus.hw.rest.RestBooksController;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookServiceImpl;
import ru.otus.hw.services.GenreService;
import ru.otus.hw.services.UserCommentService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({BooksController.class,
        GenresController.class,
        AuthorsController.class,
        LoginController.class,
        RestBooksController.class})
@Import(SecurityConfig.class)
public class MultipleEndpointsAuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

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

    private BookDto expectedBook;

    private BookFromUiDto savedBook;

    @BeforeEach
    void setUp(){
        objectMapper = new ObjectMapper();
        expectedBook = new BookDto(1L, "Title",
                new AuthorDto(1L, "Author"),
                List.of(new GenreDto(1L, "Genre")));
        savedBook = new BookFromUiDto(1L, "Book", 1L, Set.of("1", "2"));
    }

    @DisplayName("Should return expected status")
    @ParameterizedTest(name = "{0} {1} for user={2} should return {4}")
    @MethodSource("getTestData")
    void shouldReturnExpectedStatus(String method, String url, String userName, int status, boolean isRedirected) throws Exception {
        when(bookService.findById(anyLong())).thenReturn(expectedBook);
        when((bookService.insert(anyString(), anyLong(), anySet()))).thenReturn(expectedBook);

        var request = method2RequestBuilder(method, url);

        if (nonNull(userName)) {
            request = request.with(user(userName));
        }

        ResultActions resultActions = mockMvc.perform(request).andExpect(status().is(status));

        if (isRedirected) {
            resultActions.andExpect(MockMvcResultMatchers.redirectedUrlPattern("**/login"));
        }
    }

    @Test
    public void testBookEditPageNotSignedIn() throws Exception {
        mockMvc.perform(get("/book/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    private MockHttpServletRequestBuilder method2RequestBuilder(String method, String url) throws Exception {
        Map<String, MockHttpServletRequestBuilder> methodMap =
                Map.of("get", MockMvcRequestBuilders.get(url),
                        "post", MockMvcRequestBuilders.post(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(savedBook)),
                        "put", MockMvcRequestBuilders.put(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(savedBook)),
                        "delete", MockMvcRequestBuilders.delete(url));
        return methodMap.get(method);
    }

    private static Stream<Arguments> getTestData() {
        return Stream.of(
                Arguments.of("get", "/", null, 302, true),
                Arguments.of("get", "/login", null, 200, false),
                Arguments.of("get", "/book", null, 302, true),
                Arguments.of("get", "/book/1", null, 302, true),
                Arguments.of("get", "/book/edit", null, 302, true),
                Arguments.of("get", "/genre", null, 302,  true),
                Arguments.of("get", "/author", null, 302, true),

                Arguments.of("get", "/api/book", null, 302, true),
                Arguments.of("get", "/api/book/1", null, 302, true),
                Arguments.of("get", "/api/book/1/user_comment", null, 302, true),
                Arguments.of("post", "/api/book", null, 302, true),
                Arguments.of("put", "/api/book/1", null, 302, true),
                Arguments.of("delete", "/api/book/1", null, 302, true),

                Arguments.of("get", "/", "user", 200, false),
                Arguments.of("get", "/book", "user", 200, false),
                Arguments.of("get", "/book/1", "user", 200, false),
                Arguments.of("get", "/book/edit", "user", 200, false),
                Arguments.of("get", "/genre", "user", 200, false),
                Arguments.of("get", "/author", "user", 200, false),

                Arguments.of("get", "/api/book", "user", 200, false),
                Arguments.of("get", "/api/book/1", "user", 200, false),
                Arguments.of("get", "/api/book/1/user_comment", "user", 200, false),
                Arguments.of("post", "/api/book", "user", 201, false),
                Arguments.of("put", "/api/book/1", "user", 200, false),
                Arguments.of("delete", "/api/book/1", "user", 204, false)
        );
    }
}
