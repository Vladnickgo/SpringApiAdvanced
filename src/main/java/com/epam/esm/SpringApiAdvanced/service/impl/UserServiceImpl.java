package com.epam.esm.SpringApiAdvanced.service.impl;

import com.epam.esm.SpringApiAdvanced.exception.NotFoundException;
import com.epam.esm.SpringApiAdvanced.repository.UserRepository;
import com.epam.esm.SpringApiAdvanced.repository.entity.User;
import com.epam.esm.SpringApiAdvanced.service.UserService;
import com.epam.esm.SpringApiAdvanced.service.dto.UserDto;
import com.epam.esm.SpringApiAdvanced.service.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::mapEntityToDto)
                .toList();
    }

    @Override
    public UserDto findById(Integer id) {
        Optional<User> byId = userRepository.findById(id);
        User user = byId.orElseThrow(() -> new NotFoundException("User with id=" + id + " not found"));
        return userMapper.mapEntityToDto(user);
    }

    @Override
    public List<UserDto> findByName(String name) {
        return userRepository.findByLastNameContainsIgnoreCase(name).stream()
                .map(userMapper::mapEntityToDto)
                .toList();
    }

    @Override
    public UserDto save(UserDto userDto) {
        User user = userMapper.mapDtoToEntity(userDto);
        return userMapper.mapEntityToDto(userRepository.save(user));
    }
}
