package com.epam.esm.SpringApiAdvanced.service;

import com.epam.esm.SpringApiAdvanced.service.dto.OrderDto;

public interface OrderService {
    OrderDto save(Integer userId, Integer certificateId);
    OrderDto save(OrderDto orderDto);
}
