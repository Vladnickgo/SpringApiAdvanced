package com.epam.esm.SpringApiAdvanced.service.impl;

import com.epam.esm.SpringApiAdvanced.exception.NotFoundException;
import com.epam.esm.SpringApiAdvanced.repository.GiftCertificateRepository;
import com.epam.esm.SpringApiAdvanced.repository.OrderRepository;
import com.epam.esm.SpringApiAdvanced.repository.UserRepository;
import com.epam.esm.SpringApiAdvanced.repository.entity.GiftCertificate;
import com.epam.esm.SpringApiAdvanced.repository.entity.Order;
import com.epam.esm.SpringApiAdvanced.repository.entity.User;
import com.epam.esm.SpringApiAdvanced.service.OrderService;
import com.epam.esm.SpringApiAdvanced.service.dto.OrderDto;
import com.epam.esm.SpringApiAdvanced.service.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final GiftCertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;


    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, GiftCertificateRepository certificateRepository, UserRepository userRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.certificateRepository = certificateRepository;
        this.userRepository = userRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public OrderDto save(Integer userId, Integer certificateId) {
        GiftCertificate giftCertificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new NotFoundException("Certificate with id=" + certificateId + " not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
        Order order = Order.builder()
                .certificateId(giftCertificate.getId())
                .userId(user.getId())
                .orderDate(LocalDate.now())
                .orderPrice(giftCertificate.getPrice())
                .build();
        return orderMapper.mapEntityToDto(orderRepository.save(order));
    }

    @Override
    public OrderDto save(OrderDto orderDto) {
        Order order = orderMapper.mapDtoToEntity(orderDto);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.mapEntityToDto(orderRepository.save(savedOrder));
    }
}
