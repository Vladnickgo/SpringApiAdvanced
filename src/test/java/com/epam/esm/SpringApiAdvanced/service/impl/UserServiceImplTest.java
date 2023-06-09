package com.epam.esm.SpringApiAdvanced.service.impl;

import com.epam.esm.SpringApiAdvanced.exception.NotFoundException;
import com.epam.esm.SpringApiAdvanced.repository.entity.User;
import com.epam.esm.SpringApiAdvanced.repository.impl.UserRepositoryImpl;
import com.epam.esm.SpringApiAdvanced.service.dto.UserDto;
import com.epam.esm.SpringApiAdvanced.service.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    UserMapper userMapper;
    @Mock
    UserRepositoryImpl userRepository;
    @InjectMocks
    UserServiceImpl userService;

    @Test
    void testFindAll() {
        User user = User.builder()
                .id(1)
                .email("john@mail.com")
                .firstName("First")
                .lastName("Last")
                .password("1234")
                .build();
        UserDto userDto = UserDto.builder()
                .id(1)
                .email("john@mail.com")
                .firstName("First")
                .lastName("Last")
                .password("1234")
                .build();
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);
        when(userRepository.countAll()).thenReturn(1);
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.mapEntityToDto(user)).thenReturn(userDto);
        Page<UserDto> dtoPage = userService.findAll(pageable);
        assertEquals(1, dtoPage.getTotalPages());
        assertEquals(1, dtoPage.getTotalElements());
    }

    @Test
    void testFindById() {
        User user = User.builder()
                .id(1)
                .email("john@mail.com")
                .firstName("First")
                .lastName("Last")
                .password("1234")
                .build();
        UserDto userDto = UserDto.builder()
                .id(1)
                .email("john@mail.com")
                .firstName("First")
                .lastName("Last")
                .password("1234")
                .build();
        when(userRepository.findById(1)).thenReturn(Optional.ofNullable(user));
        when(userMapper.mapEntityToDto(user)).thenReturn(userDto);
        UserDto byId = userService.findById(1);
        assertEquals(userDto, byId);
    }

    @Test
    void testFindByIdIfEmpty() {
        String expectedMessage = "User with id=1 not found";
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        String message = assertThrows(NotFoundException.class, () -> userService.findById(1)).getMessage();
        assertEquals(expectedMessage, message);
    }

    @Test
    void findByName() {
        User user = User.builder()
                .id(1)
                .email("john@mail.com")
                .firstName("First")
                .lastName("Last")
                .password("1234")
                .build();
        UserDto userDto = UserDto.builder()
                .id(1)
                .email("john@mail.com")
                .firstName("First")
                .lastName("Last")
                .password("1234")
                .build();
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);
        when(userRepository.countAllByName("Last")).thenReturn(Optional.of(1));
        when(userRepository.findByLastNameContainsIgnoreCase("Last", pageable)).thenReturn(userPage);
        when(userMapper.mapEntityToDto(user)).thenReturn(userDto);
        Page<UserDto> byName = userService.findByName("Last", pageable);
        assertEquals(1, byName.getTotalElements());
        assertEquals(1, byName.getTotalPages());
    }

    @Test
    void testSave() {
        User user = User.builder()
                .id(1)
                .email("john@mail.com")
                .firstName("First")
                .lastName("Last")
                .password("1234")
                .build();
        UserDto userDto = UserDto.builder()
                .id(1)
                .email("john@mail.com")
                .firstName("First")
                .lastName("Last")
                .password("1234")
                .build();
        when(userMapper.mapDtoToEntity(userDto)).thenReturn(user);
        doNothing().when(userRepository).save(user);
        when(userRepository.findLastAdded()).thenReturn(Optional.ofNullable(user));
        when(userMapper.mapEntityToDto(user)).thenReturn(userDto);
        UserDto save = userService.save(userDto);
        assertEquals(userDto, save);
    }

    @Test
    void testSaveIfException() {
        String expectedMessage = "User not found";
        User user = User.builder()
                .id(1)
                .email("john@mail.com")
                .firstName("First")
                .lastName("Last")
                .password("1234")
                .build();
        UserDto userDto = UserDto.builder()
                .id(1)
                .email("john@mail.com")
                .firstName("First")
                .lastName("Last")
                .password("1234")
                .build();
        when(userMapper.mapDtoToEntity(userDto)).thenReturn(user);
        doNothing().when(userRepository).save(user);
        when(userRepository.findLastAdded()).thenReturn(Optional.empty());
        String message = assertThrows(NotFoundException.class, () -> userService.save(userDto)).getMessage();
        assertEquals(expectedMessage, message);
    }
}