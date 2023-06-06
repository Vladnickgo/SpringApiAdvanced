package com.epam.esm.SpringApiAdvanced.controller;

import com.epam.esm.SpringApiAdvanced.service.UserService;
import com.epam.esm.SpringApiAdvanced.service.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
    ResponseEntity<PagedModel<UserDto>> findAll(Pageable pageable) {
        Page<UserDto> all = userService.findAll(pageable);
        List<Link> userLinks = all.stream()
                .map(user -> linkTo(methodOn(UserController.class).findById(user.getId())).withSelfRel())
                .toList();
        return getPagedModelResponseEntity(pageable, all, userLinks);
    }

    @GetMapping("/{id}")
    ResponseEntity<EntityModel<UserDto>> findById(@PathVariable Integer id) {
        UserDto userDto = userService.findById(id);
        String uriString = ServletUriComponentsBuilder.fromCurrentRequest()
                .toUriString();
        Link selfLink = Link.of(uriString);
        EntityModel<UserDto> entityModel = EntityModel.of(userDto, selfLink);
        return ResponseEntity.ok(entityModel);
    }

    @GetMapping("/name/{name}")
    ResponseEntity<PagedModel<UserDto>> findByName(@PathVariable String name, Pageable pageable) {
        Page<UserDto> byName = userService.findByName(name, pageable);
        List<Link> userLinks = byName.stream()
                .map(user -> linkTo(methodOn(UserController.class).findById(user.getId())).withSelfRel())
                .toList();
        return getPagedModelResponseEntity(pageable, byName, userLinks);
    }

    @PostMapping("")
    public ResponseEntity<UserDto> save(@RequestBody UserDto userDto) {
        UserDto save = userService.save(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(save);
    }

    private ResponseEntity<PagedModel<UserDto>> getPagedModelResponseEntity(Pageable pageable, Page<UserDto> all, List<Link> userLinks) {
        Link selfLink = linkTo(methodOn(UserController.class).findAll(pageable)).withSelfRel();
        PagedModel<UserDto> pagedModel = PagedModel.of(all.getContent(), new PagedModel.PageMetadata(all.getSize(), all.getNumber(), all.getTotalElements()), userLinks);
        pagedModel.add(selfLink);
        if (all.hasPrevious()) {
            String prevLink = ServletUriComponentsBuilder.fromCurrentRequest()
                    .replaceQueryParam("page", all.getNumber() - 1)
                    .replaceQueryParam("size", all.getSize())
                    .toUriString();
            pagedModel.add(Link.of(prevLink, "prev"));
        }
        if (all.hasNext()) {
            String nextLink = ServletUriComponentsBuilder.fromCurrentRequest()
                    .replaceQueryParam("page", all.getNumber() + 1)
                    .replaceQueryParam("size", all.getSize())
                    .toUriString();
            pagedModel.add(Link.of(nextLink, "next"));
        }
        return ResponseEntity.ok(pagedModel);
    }
}
