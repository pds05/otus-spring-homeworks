package ru.otus.hw.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.otus.hw.dtos.AuthorDto;
import ru.otus.hw.dtos.BookDto;
import ru.otus.hw.dtos.BookFromUiDto;
import ru.otus.hw.dtos.GenreDto;
import ru.otus.hw.services.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import({SecurityConfig.class,
        WebAccessDeniedHandler.class,
        JacksonAutoConfiguration.class
})
public class MultipleEndpointsSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private UserCommentService userCommentService;

    private ObjectMapper objectMapper;

    private BookDto expectedBook;

    private BookFromUiDto savedBook;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        expectedBook = new BookDto(1L, "Title",
                new AuthorDto(1L, "Author"),
                List.of(new GenreDto(1L, "Genre")));
        savedBook = new BookFromUiDto(1L, "Book", 1L, Set.of("1", "2"));
    }

    @DisplayName("Should return expected status")
    @ParameterizedTest(name = "{0} {1} for user={2} should return {4}")
    @MethodSource("getTestData")
    void shouldReturnExpectedStatus(String method, String url, String userName, String[] roles, int status, String redirectedUrl) throws Exception {
        when(bookService.findById(anyLong())).thenReturn(expectedBook);
        when((bookService.insert(anyString(), anyLong(), anySet()))).thenReturn(expectedBook);

        var request = method2RequestBuilder(method, url);

        if (nonNull(userName)) {
            request = request.with(user(userName).roles(roles));
        }

        ResultActions resultActions = mockMvc.perform(request).andExpect(status().is(status));

        if (nonNull(redirectedUrl)) {
            resultActions.andExpect(MockMvcResultMatchers.redirectedUrlPattern(redirectedUrl));
        }
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
        var userRole = new String[]{"USER"};
        var editorRole = new String[]{"EDITOR"};
        return Stream.of(
                Arguments.of("get", "/", null, null, 302, "**/login"),
                Arguments.of("get", "/login", null, null, 200, null),
                Arguments.of("get", "/book", null, null, 200, null),
                Arguments.of("get", "/book/1", null, null, 302, "**/login"),
                Arguments.of("get", "/book/edit", null, null, 302, "**/login"),
                Arguments.of("get", "/genre", null, null, 302, "**/login"),
                Arguments.of("get", "/author", null, null, 302, "**/login"),

                Arguments.of("get", "/api/book", null, null, 200, null),
                Arguments.of("get", "/api/book/1", null, null, 302, "**/login"),
                Arguments.of("get", "/api/book/1/user_comment", null, null, 302, "**/login"),
                Arguments.of("post", "/api/book", null, null, 302, "**/login"),
                Arguments.of("put", "/api/book/1", null, null, 302, "**/login"),
                Arguments.of("delete", "/api/book/1", null, null, 302, "**/login"),

                Arguments.of("get", "/", "user", userRole, 200, null),
                Arguments.of("get", "/book", "user", userRole, 200, null),
                Arguments.of("get", "/book/1", "user", userRole, 200, null),
                Arguments.of("get", "/book/edit", "user", userRole, 302, "/*error/403"),
                Arguments.of("get", "/genre", "user", userRole, 200, null),
                Arguments.of("get", "/author", "user", userRole, 200, null),

                Arguments.of("get", "/api/book", "user", userRole, 200, null),
                Arguments.of("get", "/api/book/1", "user", userRole, 200, null),
                Arguments.of("get", "/api/book/1/user_comment", "user", userRole, 200, null),
                Arguments.of("post", "/api/book", "user", userRole, 302, "/*error/403"),
                Arguments.of("put", "/api/book/1", "user", userRole, 302, "/*error/403"),
                Arguments.of("delete", "/api/book/1", "user", userRole, 302, "/*error/403"),

                Arguments.of("get", "/", "editor", editorRole, 200, null),
                Arguments.of("get", "/book", "editor", editorRole, 200, null),
                Arguments.of("get", "/book/1", "editor", editorRole, 200, null),
                Arguments.of("get", "/book/edit", "editor", editorRole, 200, null),
                Arguments.of("get", "/genre", "editor", editorRole, 200, null),
                Arguments.of("get", "/author", "editor", editorRole, 200, null),

                Arguments.of("get", "/api/book", "editor", editorRole, 200, null),
                Arguments.of("get", "/api/book/1", "editor", editorRole, 200, null),
                Arguments.of("get", "/api/book/1/user_comment", "editor", editorRole, 200, null),
                Arguments.of("post", "/api/book", "editor", editorRole, 201, null),
                Arguments.of("put", "/api/book/1", "editor", editorRole, 200, null),
                Arguments.of("delete", "/api/book/1", "editor", editorRole, 204, null)
        );
    }

}
