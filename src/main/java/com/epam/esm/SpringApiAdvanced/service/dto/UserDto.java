package com.epam.esm.SpringApiAdvanced.service.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
@Builder(toBuilder = true)
public class UserDto extends RepresentationModel<UserDto> {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
