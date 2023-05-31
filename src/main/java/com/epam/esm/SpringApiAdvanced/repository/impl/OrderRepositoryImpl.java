package com.epam.esm.SpringApiAdvanced.repository.impl;

import com.epam.esm.SpringApiAdvanced.repository.OrderRepository;
import com.epam.esm.SpringApiAdvanced.repository.entity.Order;
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


@Repository
public class OrderRepositoryImpl implements OrderRepository {
    private static final String FIND_BY_USER_ID = "SELECT * FROM orders WHERE user_id = ? LIMIT ? OFFSET ? ";
    private static final String FIND_BY_ID = "SELECT * FROM orders WHERE id = ? ";
    private static final String COUNT_ALL_BY_USER_ID = "SELECT count(*) FROM orders WHERE user_id = ? ";
    private static final String SAVE = "INSERT INTO orders(certificate_id, order_date, order_price, user_id)VALUES (?,?,?,?) ";
    private static final String FIND_LAST_ADDED_ORDER = "SELECT * FROM orders " +
            "WHERE id = (SELECT max(id) FROM orders);";
    private static final String FIND_ALL = "SELECT * FROM orders LIMIT ? OFFSET ? ";
    private static final String COUNT_ALL = "SELECT count(*) FROM orders ";
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OrderRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Order entity) {
        jdbcTemplate.update(SAVE, ps -> {
            ps.setInt(1, entity.getCertificateId());
            ps.setDate(2, Date.valueOf(entity.getOrderDate()));
            ps.setInt(3, entity.getOrderPrice());
            ps.setInt(4, entity.getUserId());
        });
    }

    @Override
    public Optional<Order> findById(Integer id) {
        try {
            Order order = jdbcTemplate.queryForObject(FIND_BY_ID, new Object[]{id}, (rs, rowNum) -> Order.builder()
                    .id(rs.getInt("id"))
                    .certificateId(rs.getInt("certificate_id"))
                    .orderDate(rs.getDate("order_date").toLocalDate())
                    .orderPrice(rs.getInt("order_price"))
                    .userId(rs.getInt("user_id"))
                    .build());
            return Optional.ofNullable(order);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public Page<Order> findAll(Pageable pageable) {
        Integer total = countAll();
        List<Order> orderList = jdbcTemplate.query(FIND_ALL, ps -> {
            ps.setInt(1, pageable.getPageSize());
            ps.setInt(2, (int) pageable.getOffset());
        }, (rs, rowNum) -> Order.builder()
                .id(rs.getInt("id"))
                .certificateId(rs.getInt("certificate_id"))
                .orderDate(rs.getDate("order_date").toLocalDate())
                .orderPrice(rs.getInt("order_price"))
                .userId(rs.getInt("user_id"))
                .build());
        return new PageImpl<>(orderList, pageable, total);
    }

    @Override
    public void update(Integer integer, Order entity) {

    }

    @Override
    public void deleteById(Integer integer) {

    }

    @Override
    public Page<Order> findByUserId(Integer userId, Pageable pageable) {
        Integer total = countAllByUserId(userId);
        List<Order> ordersByUserId = jdbcTemplate.query(FIND_BY_USER_ID, ps -> {
            ps.setInt(1, userId);
            ps.setInt(2, pageable.getPageSize());
            ps.setInt(3, (int) pageable.getOffset());
        }, (rs, rowNum) -> Order.builder()
                .id(rs.getInt("id"))
                .certificateId(rs.getInt("certificate_id"))
                .orderDate(rs.getDate("order_date").toLocalDate())
                .orderPrice(rs.getInt("order_price"))
                .userId(rs.getInt("user_id"))
                .build());
        return new PageImpl<>(ordersByUserId, pageable, total);
    }

    @Override
    public Order findLastAddedOrder() {
        return jdbcTemplate.queryForObject(FIND_LAST_ADDED_ORDER, (rs, rowNum) -> Order.builder()
                .id(rs.getInt("id"))
                .certificateId(rs.getInt("certificate_id"))
                .orderDate(rs.getDate("order_date").toLocalDate())
                .orderPrice(rs.getInt("order_price"))
                .userId(rs.getInt("user_id"))
                .build());
    }

    public Integer countAllByUserId(Integer userId) {
        return jdbcTemplate.queryForObject(COUNT_ALL_BY_USER_ID, new Object[]{userId}, Integer.class);
    }

    public Integer countAll() {
        return jdbcTemplate.queryForObject(COUNT_ALL, Integer.class);
    }
}
