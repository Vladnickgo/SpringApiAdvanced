package com.epam.esm.SpringApiAdvanced.service;

import com.epam.esm.SpringApiAdvanced.service.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserDto> findAll(Pageable pageable);

    UserDto findById(Integer id);

    Page<UserDto> findByName(String name, Pageable pageable);

    UserDto save(UserDto userDto);
}
