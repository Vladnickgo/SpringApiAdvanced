package com.epam.esm.SpringApiAdvanced.service.mapper;

import com.epam.esm.SpringApiAdvanced.repository.entity.Order;
import com.epam.esm.SpringApiAdvanced.service.dto.OrderDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    Order mapDtoToEntity(OrderDto orderDto);


    OrderDto mapEntityToDto(Order order);
}

