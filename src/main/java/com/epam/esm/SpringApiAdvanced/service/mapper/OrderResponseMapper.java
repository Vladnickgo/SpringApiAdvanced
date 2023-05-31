package com.epam.esm.SpringApiAdvanced.service.mapper;

import com.epam.esm.SpringApiAdvanced.repository.entity.Order;
import com.epam.esm.SpringApiAdvanced.service.dto.OrderResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderResponseMapper {
    Order mapDtoToEntity(OrderResponseDto orderResponseDto);
    OrderResponseDto mapEntityToDto(Order order);
}
