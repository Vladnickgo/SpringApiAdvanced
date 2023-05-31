package com.epam.esm.SpringApiAdvanced.service.mapper;

import com.epam.esm.SpringApiAdvanced.repository.entity.User;
import com.epam.esm.SpringApiAdvanced.service.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto mapEntityToDto(User user);


    User mapDtoToEntity(UserDto userDto);
}

