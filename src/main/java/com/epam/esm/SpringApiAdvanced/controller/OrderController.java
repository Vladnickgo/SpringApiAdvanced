package com.epam.esm.SpringApiAdvanced.controller;

import com.epam.esm.SpringApiAdvanced.service.OrderService;
import com.epam.esm.SpringApiAdvanced.service.dto.OrderDto;
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
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("")
    public ResponseEntity<PagedModel<OrderDto>> findAll(Pageable pageable) {
        Page<OrderDto> dtoPage = orderService.findAll(pageable);
        List<Link> orderLink = dtoPage.stream()
                .map(orderDto -> linkTo(methodOn(OrderController.class).findById(orderDto.getId())).withSelfRel())
                .toList();
        PagedModel<OrderDto> pagedModel = PagedModel.of(dtoPage.getContent(), new PagedModel.PageMetadata(dtoPage.getSize(), dtoPage.getNumber(), dtoPage.getTotalElements()), orderLink);
        pagedModel.add(orderLink);
        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<OrderDto> saveOrder(@RequestParam("user") int userId,
                                              @RequestParam("certificate") int certificateId) {
        OrderDto save = orderService.save(userId, certificateId);
        return ResponseEntity.ok(save);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<OrderDto>> findById(@PathVariable Integer id) {
        OrderDto orderDto = orderService.findById(id);
        String uriString = ServletUriComponentsBuilder.fromCurrentRequest()
                .toUriString();
        String findAllString = ServletUriComponentsBuilder.fromCurrentContextPath().replacePath("/order").toUriString();
        Link selfLink = Link.of(uriString);
        Link findAllLink = Link.of(findAllString).withRel("findAll");
        EntityModel<OrderDto> entityModel = EntityModel.of(orderDto, selfLink);
        entityModel.add(findAllLink);
        return ResponseEntity.ok(entityModel);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<PagedModel<OrderDto>> getUserOrders(@PathVariable Integer userId, Pageable pageable) {
        Page<OrderDto> dtoPage = orderService.getUserOrders(userId, pageable);
        List<Link> orderLink = dtoPage.stream()
                .map(orderDto -> linkTo(methodOn(OrderController.class).getUserOrders(userId, pageable)).withSelfRel())
                .toList();
        return getPagedModelResponseEntity(pageable, dtoPage, orderLink, userId);
    }

    private ResponseEntity<PagedModel<OrderDto>> getPagedModelResponseEntity(Pageable pageable, Page<OrderDto> all, List<Link> orderLinks, Integer userId) {
        Link selfLink = linkTo(methodOn(OrderController.class).getUserOrders(userId, pageable)).withSelfRel();
        PagedModel<OrderDto> pagedModel = PagedModel.of(all.getContent(), new PagedModel.PageMetadata(all.getSize(), all.getNumber(), all.getTotalElements()), orderLinks);
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
