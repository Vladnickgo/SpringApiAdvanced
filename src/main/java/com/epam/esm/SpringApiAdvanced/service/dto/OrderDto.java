package com.epam.esm.SpringApiAdvanced.service.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
@Builder
public class OrderDto extends RepresentationModel<OrderDto> {
    private Integer id;
    private UserDto userDto;
    private GiftCertificateDto giftCertificateDto;
    private Integer orderPrice;
    private LocalDate orderDate;
}
