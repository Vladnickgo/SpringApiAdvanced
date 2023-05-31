package com.epam.esm.SpringApiAdvanced.controller;

import com.epam.esm.SpringApiAdvanced.service.OrderService;
import com.epam.esm.SpringApiAdvanced.service.dto.OrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
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

    @PostMapping("/")
    public ResponseEntity<OrderDto> saveOrder(@RequestParam("user") int userId,
                                              @RequestParam("certificate") int certificateId) {
        OrderDto save = orderService.save(userId, certificateId);
        return ResponseEntity.ok(save);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<OrderDto>> getOrderById(@PathVariable Integer id) {
        OrderDto orderDto = orderService.findById(id);
        String uriString = ServletUriComponentsBuilder.fromCurrentRequest()
                .toUriString();
        Link selfLink = Link.of(uriString);
        EntityModel<OrderDto> entityModel = EntityModel.of(orderDto, selfLink);
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
