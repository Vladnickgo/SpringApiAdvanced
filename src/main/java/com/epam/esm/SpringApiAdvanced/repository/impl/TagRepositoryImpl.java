package com.epam.esm.SpringApiAdvanced.repository.impl;

import com.epam.esm.SpringApiAdvanced.repository.TagRepository;
import com.epam.esm.SpringApiAdvanced.repository.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TagRepositoryImpl implements TagRepository {
    private static final String FIND_ALL = "SELECT * FROM tags LIMIT ? OFFSET ? ";
    private static final String FIND_BY_ID = "SELECT * FROM tags WHERE id=? ";
    private static final String FIND_BY_PART_OF_NAME_ASC = "SELECT * FROM tags WHERE name LIKE CONCAT('%',?,'%') LIMIT ? OFFSET ? ";
    private static final String FIND_BY_PART_OF_NAME_DESC = "SELECT * FROM tags WHERE name LIKE CONCAT('%',?,'%') LIMIT ? OFFSET ? ORDER BY name DESC ";
    private static final String SAVE = "INSERT INTO tags(name) VALUES (?)";
    private static final String FIND_MOST_USED_TAG = "SELECT t.id, t.name " +
            "FROM users " +
            "         LEFT JOIN orders o on users.id = o.user_id " +
            "         LEFT JOIN certificate_tag ct on o.certificate_id = ct.certificate_id " +
            "         LEFT JOIN tags t on t.id = ct.tag_id " +
            "WHERE users.id = (SELECT id " +
            "                  FROM users " +
            "                           LEFT JOIN orders o2 on users.id = o2.user_id " +
            "                  GROUP BY id " +
            "                  ORDER BY sum(order_price) DESC " +
            "                  LIMIT 1) " +
            "GROUP BY users.id, t.id " +
            "ORDER BY t.id DESC " +
            "LIMIT 1 ";
    private static final String UPDATE_TAG_BY_ID = "UPDATE tags SET name=? WHERE id=? ";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TagRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Tag findByName(String tagName) {
        return null;
    }

    @Override
    public List<Tag> findByNameContainingIgnoreCase(String partOfName, Pageable pageable) {
        Sort sort = pageable.getSort();
        String query = sort.toString().equals("name: ASC") ? FIND_BY_PART_OF_NAME_ASC : FIND_BY_PART_OF_NAME_DESC;
        return jdbcTemplate.query(query, ps -> {
            ps.setString(1, partOfName);
            ps.setInt(2, pageable.getPageSize());
            ps.setInt(3, pageable.getPageNumber());
        }, (rs, rowNum) -> {
            Tag tag = new Tag();
            tag.setId(rs.getInt("id"));
            tag.setName(rs.getString("name"));
            return tag;
        });
    }

    @Override
    public Tag findMostWidelyUsedTag() {
        return jdbcTemplate.queryForObject(FIND_MOST_USED_TAG, (rs, rowNum) -> Tag.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build());
    }

    @Override
    public void save(Tag entity) {
        jdbcTemplate.update(SAVE, entity.getName());
    }

    @Override
    public Optional<Tag> findById(Integer id) {
        try {
            Tag tag = jdbcTemplate.queryForObject(FIND_BY_ID, new Object[]{id}, (rs, rowNum) -> {
                Tag tagMap = new Tag();
                tagMap.setId(rs.getInt("id"));
                tagMap.setName(rs.getString("name"));
                return tagMap;
            });
            return Optional.ofNullable(tag);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public Page<Tag> findAll(Pageable pageable) {
        int total = countAll();
        List<Tag> tagList = jdbcTemplate.query(FIND_ALL, ps -> {
            ps.setInt(1, pageable.getPageSize());
            ps.setInt(2, pageable.getPageNumber());
        }, (rs, rowNum) -> {
            Tag tag = new Tag();
            tag.setId(rs.getInt("id"));
            tag.setName(rs.getString("name"));
            return tag;
        });
        return new PageImpl<>(tagList, pageable, total);
    }

    @Override
    public void update(Integer id, Tag entity) {
        jdbcTemplate.update(UPDATE_TAG_BY_ID, ps -> {
            ps.setString(1, entity.getName());
            ps.setInt(2, id);
        });
    }

    @Override
    public void deleteById(Integer integer) {

    }

    @Override
    public Integer countAll() {
        return jdbcTemplate.queryForObject("SELECT count(*) FROM tags", Integer.class);
    }
}
