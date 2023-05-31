package com.epam.esm.SpringApiAdvanced.repository.impl;

import com.epam.esm.SpringApiAdvanced.exception.DataBaseRuntimeException;
import com.epam.esm.SpringApiAdvanced.repository.UserRepository;
import com.epam.esm.SpringApiAdvanced.repository.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private static final String FIND_ALL = "SELECT * FROM users LIMIT ? OFFSET ? ";
    private static final String COUNT_ALL = "SELECT count(*) AS number FROM users ";
    private static final String COUNT_ALL_BY_NAME = "SELECT count(*) AS number FROM users WHERE last_name LIKE CONCAT('%',?,'%') ";
    private static final String FIND_BY_ID = "SELECT * FROM users WHERE id=?";
    private static final String FIND_ALL_BY_NAME = "SELECT * FROM users WHERE last_name LIKE CONCAT('%',?,'%') LIMIT ? OFFSET ? ";
    private static final String SAVE = "INSERT INTO users(email, first_name, last_name, password) " +
            "VALUES (?, ?, ?, ?) ";
    public static final String FIND_LAST_ADDED = "SELECT * FROM users " +
            "WHERE id = (SELECT max(id) FROM users) ";
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(User entity) {
        jdbcTemplate.update(SAVE, ps -> {
            ps.setString(1, entity.getEmail());
            ps.setString(2, entity.getFirstName());
            ps.setString(3, entity.getLastName());
            ps.setString(4, entity.getPassword());
        });
    }

    @Override
    public Optional<User> findById(Integer id) {
        try {
            User user = jdbcTemplate.queryForObject(FIND_BY_ID, new Object[]{id}, (rs, rowNum) -> User.builder()
                    .id(rs.getInt("id"))
                    .email(rs.getString("email"))
                    .firstName(rs.getString("first_name"))
                    .lastName(rs.getString("last_name"))
                    .password(rs.getString("password"))
                    .build());
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        int total = countAll();
        List<User> userList = jdbcTemplate.query(FIND_ALL, ps -> {
            ps.setInt(1, pageable.getPageSize());
            ps.setInt(2, (int) pageable.getOffset());
        }, (rs, rowNum) -> User.builder()
                .id(rs.getInt("id"))
                .email(rs.getString("email"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .password(rs.getString("password"))
                .build());
        return new PageImpl<>(userList, pageable, total);
    }

    @Override
    public void update(Integer integer, User entity) {

    }

    @Override
    public void deleteById(Integer integer) {

    }

    public Integer countAll() {
        return jdbcTemplate.queryForObject(COUNT_ALL, Integer.class);
    }

    public Page<User> findByLastNameContainsIgnoreCase(String name, Pageable pageable) {
        int total = countAllByName(name).orElseThrow(() -> new DataBaseRuntimeException("Users with lastName=" + name + " not found"));
        List<User> userList = jdbcTemplate.query(FIND_ALL_BY_NAME, ps -> {
            ps.setString(1, name);
            ps.setInt(2, pageable.getPageSize());
            ps.setInt(3, (int) pageable.getOffset());
        }, (rs, rowNum) -> User.builder()
                .id(rs.getInt("id"))
                .email(rs.getString("email"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .password(rs.getString("password"))
                .build());
        return new PageImpl<>(userList, pageable, total);
    }

    public Optional<Integer> countAllByName(String name) {
        return Optional.ofNullable(jdbcTemplate.queryForObject(COUNT_ALL_BY_NAME, new Object[]{name}, Integer.class));
    }

    public Optional<User> findLastAdded() {
        try {
            User user = jdbcTemplate.queryForObject(FIND_LAST_ADDED, (rs, rowNum) -> User.builder()
                    .id(rs.getInt("id"))
                    .email(rs.getString("email"))
                    .firstName(rs.getString("first_name"))
                    .lastName(rs.getString("last_name"))
                    .password(rs.getString("password"))
                    .build());
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }
}
