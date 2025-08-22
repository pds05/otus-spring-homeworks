package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class JdbcGenreRepository implements GenreRepository {

    private final JdbcOperations jdbcOperations;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Genre> findAll() {
        return jdbcOperations.query("select id, name from genres", new GnreRowMapper());
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("ids", ids);
        return namedParameterJdbcTemplate.query("select id, name from genres where id in (:ids)",
                namedParameters, new GnreRowMapper());
    }

    private static class GnreRowMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet rs, int i) throws SQLException {
            Genre genre = new Genre();

            genre.setId(rs.getLong("id"));
            genre.setName(rs.getString("name"));

            return genre;
        }
    }
}
