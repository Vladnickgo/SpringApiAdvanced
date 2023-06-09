package com.epam.esm.SpringApiAdvanced.service.impl;

import com.epam.esm.SpringApiAdvanced.exception.NotFoundException;
import com.epam.esm.SpringApiAdvanced.repository.entity.GiftCertificate;
import com.epam.esm.SpringApiAdvanced.repository.entity.Order;
import com.epam.esm.SpringApiAdvanced.repository.entity.User;
import com.epam.esm.SpringApiAdvanced.repository.impl.GiftCertificateRepositoryImpl;
import com.epam.esm.SpringApiAdvanced.repository.impl.OrderRepositoryImpl;
import com.epam.esm.SpringApiAdvanced.repository.impl.UserRepositoryImpl;
import com.epam.esm.SpringApiAdvanced.service.dto.OrderDto;
import com.epam.esm.SpringApiAdvanced.service.mapper.OrderMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock
    OrderRepositoryImpl orderRepository;
    @Mock
    OrderMapper orderMapper;
    @Mock
    GiftCertificateRepositoryImpl certificateRepository;
    @Mock
    UserRepositoryImpl userRepository;
    @InjectMocks
    OrderServiceImpl orderService;

       @Test
    void testSave() {
        GiftCertificate certificate = GiftCertificate.builder()
                .id(1)
                .name("Alpha")
                .price(19)
                .description("Crete")
                .duration(54)
                .createDate(LocalDate.of(2021, 7, 16))
                .lastUpdateDate(LocalDate.of(2025, 8, 1))
                .build();
        User user= User.builder()
                .id(1)
                .email("john@mail.com")
                .firstName("John")
                .lastName("Doe")
                .password("1234")
                .build();
        Order order = Order.builder()
                .orderDate(LocalDate.now())
                .orderPrice(19)
                .userId(1)
                .certificateId(1)
                .build();
        OrderDto orderDto = OrderDto.builder()
                .orderDate(LocalDate.now())
                .orderPrice(19)
                .userId(1)
                .certificateId(1)
                .build();
        when(userRepository.findById(1)).thenReturn(Optional.ofNullable(user));
        when(certificateRepository.findById(1)).thenReturn(Optional.ofNullable(certificate));
        doNothing().when(orderRepository).save(order);
        when(orderRepository.findLastAddedOrder()).thenReturn(order);
        when(orderMapper.mapEntityToDto(order)).thenReturn(orderDto);
        OrderDto actual = orderService.save(1, 1);
        verify(orderRepository,times(1)).save(order);
        verify(certificateRepository,times(1)).findById(1);
        verify(userRepository,times(1)).findById(1);
        assertEquals(orderDto,actual);
    }

    @Test
    void getUserOrders() {
        List<Order> orderList = List.of(Order.builder()
                .id(1)
                .orderDate(LocalDate.of(2023, 1, 1))
                .orderPrice(107)
                .userId(1)
                .certificateId(1)
                .build());
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<Order> orderPage = new PageImpl<>(orderList, pageable, 1);
        when(orderRepository.countAllByUserId(1)).thenReturn(1);
        when(orderRepository.findByUserId(1, pageable)).thenReturn(orderPage);
        when(orderMapper.mapEntityToDto(any(Order.class))).thenAnswer(invocation -> {
            Order argument = invocation.getArgument(0);
            return OrderDto.builder()
                    .id(argument.getId())
                    .orderPrice(argument.getOrderPrice())
                    .orderDate(argument.getOrderDate())
                    .userId(argument.getUserId())
                    .certificateId(argument.getCertificateId())
                    .build();
        });
        Page<OrderDto> userOrders = orderService.getUserOrders(1, pageable);
        verify(orderRepository).countAllByUserId(1);
        verify(orderRepository).findByUserId(1, pageable);
        assertEquals(1, userOrders.getTotalElements());
        assertEquals(1, userOrders.getTotalPages());
    }

    @Test
    void testFindById() {
        Order order = Order.builder()
                .id(1)
                .orderDate(LocalDate.of(2023, 1, 1))
                .orderPrice(107)
                .userId(1)
                .certificateId(1)
                .build();
        when(orderRepository.findById(1)).thenReturn(Optional.ofNullable(order));
        when(orderMapper.mapEntityToDto(any(Order.class))).thenAnswer(invocation -> {
            Order argument = invocation.getArgument(0);
            return OrderDto.builder()
                    .id(argument.getId())
                    .orderPrice(argument.getOrderPrice())
                    .orderDate(argument.getOrderDate())
                    .userId(argument.getUserId())
                    .certificateId(argument.getCertificateId())
                    .build();
        });
        OrderDto actual = orderService.findById(1);
        verify(orderRepository).findById(1);
        assertEquals(1, actual.getId());
        assertEquals(107, actual.getOrderPrice());
        assertEquals(1, actual.getUserId());
        assertEquals(1, actual.getCertificateId());
        assertEquals(LocalDate.of(2023, 1, 1), actual.getOrderDate());
    }

    @Test
    void testFindByIdOfNullable() {
        String expectedMessage = "Order with id=1 not found";
        when(orderRepository.findById(1)).thenReturn(Optional.empty());
        String message = assertThrows(NotFoundException.class, () -> orderService.findById(1)).getMessage();
        assertEquals(expectedMessage, message);
    }

    @Test
    void findAll() {
        List<Order> orderList = List.of(Order.builder()
                .id(1)
                .orderDate(LocalDate.of(2023, 1, 1))
                .orderPrice(107)
                .userId(1)
                .certificateId(1)
                .build());
        OrderDto orderDto = OrderDto.builder()
                .id(1)
                .orderDate(LocalDate.of(2023, 1, 1))
                .orderPrice(107)
                .userId(1)
                .certificateId(1)
                .build();
        List<OrderDto> orderDtoList = List.of(orderDto);
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<Order> orderPage = new PageImpl<>(orderList, pageable, 1);
        Page<OrderDto> expectedPage = new PageImpl<>(orderDtoList, pageable, 1);
        when(orderRepository.countAll()).thenReturn(1);
        when(orderRepository.findAll(pageable)).thenReturn(orderPage);
        when(orderMapper.mapEntityToDto(any(Order.class))).thenAnswer(invocation -> {
            Order argument = invocation.getArgument(0);
            return OrderDto.builder()
                    .id(argument.getId())
                    .orderPrice(argument.getOrderPrice())
                    .orderDate(argument.getOrderDate())
                    .userId(argument.getUserId())
                    .certificateId(argument.getCertificateId())
                    .build();
        });

        Page<OrderDto> dtoPage = orderService.findAll(pageable);
        assertEquals(expectedPage, dtoPage);
        assertEquals(1, dtoPage.getContent().size());
        assertEquals(1, dtoPage.getTotalElements());
        assertEquals(1, dtoPage.getTotalPages());
        assertEquals(0, dtoPage.getNumber());
        assertFalse(dtoPage.hasNext());
        assertFalse(dtoPage.hasPrevious());
    }
}