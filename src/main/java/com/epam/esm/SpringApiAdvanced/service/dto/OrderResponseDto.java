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
public class OrderResponseDto extends RepresentationModel<OrderResponseDto> {
    private Integer certificateId;
    private Integer orderPrice;
    private LocalDate orderDate;
}
