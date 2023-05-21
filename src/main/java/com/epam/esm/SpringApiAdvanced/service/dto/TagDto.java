package com.epam.esm.SpringApiAdvanced.service.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class TagDto extends RepresentationModel<TagDto> {
    private Integer id;
    private String name;
}
