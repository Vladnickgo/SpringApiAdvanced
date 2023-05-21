package com.epam.esm.SpringApiAdvanced.controller;

import com.epam.esm.SpringApiAdvanced.service.UserService;
import com.epam.esm.SpringApiAdvanced.service.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ResponseBody
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> all = userService.findAll();
        System.out.println(all);
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{id}")
    ResponseEntity<UserDto> findById(@PathVariable Integer id) {
        UserDto userDto = userService.findById(id);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/name/{name}")
    ResponseEntity<List<UserDto>> findByName(@PathVariable String name) {
        List<UserDto> byName = userService.findByName(name);
        return ResponseEntity.ok(byName);
    }

    @PostMapping("")
    public ResponseEntity<UserDto> save(@RequestBody UserDto userDto) {
        UserDto save = userService.save(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(save);
//        return null;
    }
}
