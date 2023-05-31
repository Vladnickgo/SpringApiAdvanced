package com.epam.esm.SpringApiAdvanced.repository.impl;

import com.epam.esm.SpringApiAdvanced.repository.GiftCertificateRepository;
import com.epam.esm.SpringApiAdvanced.repository.entity.GiftCertificate;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class GiftCertificateRepositoryImpl implements GiftCertificateRepository {
    private static final String FIND_ALL = "SELECT * FROM certificates LIMIT ? OFFSET ? ";
    private static final String FIND_BY_ID = "SELECT * FROM certificates WHERE id = ? ";
    private static final String SAVE = "INSERT INTO certificates(create_date, description, duration, last_update_date, name, price) " +
            "VALUES (?,?,?,?,?,?) ";
    private static final String UPDATE = "UPDATE certificates " +
            "SET create_date=?, description=?, duration=?, last_update_date=?, name=?, price=? " +
            "WHERE id = ? ";

    public static final String FIND_BY_SEVERAL_TAGS = "SELECT certificates.id, create_date, " +
            "       description, duration, last_update_date, certificates.name, price, " +
            "       count(certificates.id) AS number " +
            "FROM certificates " +
            "         left join certificate_tag ct on certificates.id = ct.certificate_id " +
            "         left join tags t on t.id = ct.tag_id " +
            "WHERE t.name IN (%s) " +
            "GROUP BY certificates.id " +
            "HAVING number = ? " +
            "ORDER BY certificates.id " +
            "LIMIT ? OFFSET ? ";
    public static final String COUNT_CERTIFICATES_BY_SEVERAL_TAGS = "SELECT COUNT(*) AS count " +
            "FROM (SELECT certificates.id, create_date, " +
            "            description, duration, last_update_date, certificates.name, price, " +
            "            count(certificates.id) AS number " +
            "            FROM certificates " +
            "                       LEFT JOIN certificate_tag ct on certificates.id = ct.certificate_id " +
            "                       LEFT JOIN tags t on t.id = ct.tag_id " +
            "            WHERE t.name IN (%s) " +
            "            GROUP BY certificates.id " +
            "            HAVING number = ? )  AS sub_query ";
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GiftCertificateRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(GiftCertificate entity) {
        jdbcTemplate.update(SAVE, ps -> {
            ps.setDate(1, Date.valueOf(entity.getCreateDate()));
            ps.setString(2, entity.getDescription());
            ps.setInt(3, entity.getDuration());
            ps.setDate(4, Date.valueOf(entity.getLastUpdateDate()));
            ps.setString(5, entity.getName());
            ps.setInt(6, entity.getPrice());
        });
    }

    @Override
    public Optional<GiftCertificate> findById(Integer id) {
        try {
            GiftCertificate giftCertificate = jdbcTemplate.queryForObject(FIND_BY_ID, new Object[]{id}, (rs, rowNum) -> GiftCertificate.builder()
                    .id(rs.getInt("id"))
                    .createDate(rs.getDate("create_date").toLocalDate())
                    .description(rs.getString("description"))
                    .duration(rs.getInt("duration"))
                    .lastUpdateDate(rs.getDate("last_update_date").toLocalDate())
                    .name(rs.getString("name"))
                    .price(rs.getInt("price"))
                    .build());
            return Optional.ofNullable(giftCertificate);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public Page<GiftCertificate> findAll(Pageable pageable) {
        Integer total = countAll();
        List<GiftCertificate> giftCertificateList = jdbcTemplate.query(FIND_ALL, ps -> {
            ps.setInt(1, pageable.getPageSize());
            ps.setInt(2, (int) pageable.getOffset());
        }, (rs, rowNum) -> GiftCertificate.builder()
                .id(rs.getInt("id"))
                .createDate(rs.getDate("create_date").toLocalDate())
                .description(rs.getString("description"))
                .duration(rs.getInt("duration"))
                .lastUpdateDate(rs.getDate("last_update_date").toLocalDate())
                .name(rs.getString("name"))
                .price(rs.getInt("price"))
                .build());
        return new PageImpl<>(giftCertificateList, pageable, total);
    }

    @Override
    public void update(Integer integer, GiftCertificate entity) {
        jdbcTemplate.update(UPDATE, ps -> {
            ps.setDate(1, Date.valueOf(entity.getCreateDate()));
            ps.setString(2, entity.getDescription());
            ps.setInt(3, entity.getDuration());
            ps.setDate(4, Date.valueOf(entity.getLastUpdateDate()));
            ps.setString(5, entity.getName());
            ps.setInt(6, entity.getPrice());
            ps.setInt(7, integer);
        });
    }

    @Override
    public void deleteById(Integer integer) {

    }

    public Integer countAll() {
        return jdbcTemplate.queryForObject("SELECT count(*) FROM certificates", Integer.class);
    }

    @Override
    @Transactional
    public Page<GiftCertificate> findBySeveralTags(Set<String> namesSet, Pageable pageable) {
        String namesString = getNamesString(namesSet);
        Integer total = countCertificatesBySeveralTags(namesSet);
        List<GiftCertificate> certificateList = jdbcTemplate.query(String.format(FIND_BY_SEVERAL_TAGS, namesString), ps -> {
            ps.setInt(1, namesSet.size());
            ps.setInt(2, pageable.getPageSize());
            ps.setInt(3, (int) pageable.getOffset());
        }, (rs, rowNum) -> GiftCertificate.builder()
                .id(rs.getInt("id"))
                .createDate(rs.getDate("create_date").toLocalDate())
                .description(rs.getString("description"))
                .duration(rs.getInt("duration"))
                .lastUpdateDate(rs.getDate("last_update_date").toLocalDate())
                .name(rs.getString("name"))
                .price(rs.getInt("price"))
                .build());
        return new PageImpl<>(certificateList, pageable, total);
    }

    private static String getNamesString(Set<String> namesSet) {
        return namesSet.stream().collect(Collectors.joining("', '", "'", "'"));
    }

    public Integer countCertificatesBySeveralTags(Set<String> namesSet) {
        String namesString = getNamesString(namesSet);
        int setSize = namesSet.size();
        String query = String.format(COUNT_CERTIFICATES_BY_SEVERAL_TAGS, namesString);
        return jdbcTemplate.queryForObject(query, new Object[]{setSize}, Integer.class);
    }
}
