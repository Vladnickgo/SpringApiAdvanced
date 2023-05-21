package com.epam.esm.SpringApiAdvanced.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class GiftCertificateDto extends RepresentationModel<GiftCertificateDto> {
    private Integer id;
    private String name;
    private String description;
    private Integer price;
    private Integer duration;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate createDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate lastUpdateDate;
}
