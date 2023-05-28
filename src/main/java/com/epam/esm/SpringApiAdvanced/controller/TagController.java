package com.epam.esm.SpringApiAdvanced.controller;

import com.epam.esm.SpringApiAdvanced.service.TagService;
import com.epam.esm.SpringApiAdvanced.service.dto.TagDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@ResponseBody
@RequestMapping("/tag")
@EnableHypermediaSupport(type = {EnableHypermediaSupport.HypermediaType.HAL})
public class TagController {

    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("")
    public ResponseEntity<PagedModel<TagDto>> findAll(Pageable pageable) {
        Page<TagDto> all = tagService.findAll(pageable);
        List<Link> tagLinks = all.stream()
                .map(tag -> linkTo(methodOn(TagController.class).findById(tag.getId())).withSelfRel())
                .collect(Collectors.toList());
        return getPagedModelResponseEntity(pageable, all, tagLinks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<TagDto>> findById(@PathVariable Integer id) {
        TagDto byId = tagService.findById(id);
        String uriString = ServletUriComponentsBuilder.fromCurrentRequest()
                .toUriString();
        Link selfLink = Link.of(uriString);
        EntityModel<TagDto> entityModel = EntityModel.of(byId, selfLink);
        return ResponseEntity.ok(entityModel);
    }

    @GetMapping("/name/{partOfName}")
    public ResponseEntity<PagedModel<TagDto>> findByPartOfName(@PathVariable String partOfName,
                                                               @PageableDefault(page = 1, sort = "name") Pageable pageable) {
        Page<TagDto> all = tagService.findAllByTagName(partOfName, pageable);
        List<Link> tagLinks = all.stream()
                .map(tag -> linkTo(methodOn(TagController.class).findById(tag.getId())).withSelfRel())
                .toList();
        return getPagedModelResponseEntity(pageable, all, tagLinks);
    }

    private ResponseEntity<PagedModel<TagDto>> getPagedModelResponseEntity(@PageableDefault(page = 1, sort = "name") Pageable pageable, Page<TagDto> all, List<Link> tagLinks) {
        Link selfLink = linkTo(methodOn(TagController.class).findAll(pageable)).withSelfRel();
        PagedModel<TagDto> pagedModel = PagedModel.of(all.getContent(), new PagedModel.PageMetadata(all.getSize(), all.getNumber(), all.getTotalElements()), tagLinks);
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

    @PostMapping("/")
    @ResponseBody
    public ResponseEntity<TagDto> save(@RequestBody TagDto tagDto) {
        TagDto save = tagService.save(tagDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(save);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagDto> update(@PathVariable Integer id, @RequestBody TagDto tagDto) {
        TagDto update = tagService.update(id, tagDto);
        return ResponseEntity.ok(update);
    }

    @GetMapping("/mostWidely")
    public ResponseEntity<EntityModel<TagDto>> getMostWidelyUsedTag() {
        TagDto mostWidelyUsedTag = tagService.findMostWidelyUsedTag();
        String uriString = ServletUriComponentsBuilder.fromCurrentRequest()
                .toUriString();
        Link selfLink = Link.of(uriString);
        EntityModel<TagDto> entityModel = EntityModel.of(mostWidelyUsedTag, selfLink);
        return ResponseEntity.ok(entityModel);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<EntityModel<TagDto>> updateById(@PathVariable Integer id, @RequestBody TagDto tagDto) {
        TagDto update = tagService.update(id, tagDto);
        Link selfLink = linkTo(methodOn(TagController.class).findById(id)).withSelfRel();
        EntityModel<TagDto> entityModel = EntityModel.of(update, selfLink);
        return ResponseEntity.ok(entityModel);
    }
}