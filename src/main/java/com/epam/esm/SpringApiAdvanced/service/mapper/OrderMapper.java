package com.epam.esm.SpringApiAdvanced.service.mapper;

import com.epam.esm.SpringApiAdvanced.repository.entity.Order;
import com.epam.esm.SpringApiAdvanced.service.dto.OrderDto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mappings({
            @Mapping(target = "orderId", source = "id"),
            @Mapping(target = "userId", source = "userDto.id"),
            @Mapping(target = "certificateId", source = "giftCertificateDto.id"),
            @Mapping(target = "orderPrice", source = "orderPrice"),
            @Mapping(target = "orderDate", source = "orderDate")
    })
    Order mapDtoToEntity(OrderDto orderDto);

    @InheritInverseConfiguration
    OrderDto mapEntityToDto(Order order);
}

