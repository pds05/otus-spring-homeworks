package ru.otus.hw.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class BookFromUiDto {

    private long id;

    private String title;

    private long authorId;

    private Set<Long> genreIds;

}
