package com.epam.esm.SpringApiAdvanced.controller;

import com.epam.esm.SpringApiAdvanced.service.TagService;
import com.epam.esm.SpringApiAdvanced.service.dto.TagDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Page<TagDto>> findAll(Pageable pageable) {
        Page<TagDto> all = tagService.findAll(pageable);
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDto> findById(@PathVariable Integer id) {
        TagDto byId = tagService.findById(id);
        return ResponseEntity.ok(byId);
    }

    @GetMapping("/name/{partOfName}")
    public ResponseEntity<Page<TagDto>> findByPartOfName(@PathVariable String partOfName, Pageable pageable) {

        Page<TagDto> allByTagName = tagService.findAllByTagName(partOfName, pageable);
        return ResponseEntity.ok(allByTagName);
    }

    @PostMapping("/")
    public ResponseEntity<TagDto> save(@RequestBody TagDto tagDto) {
        TagDto save = tagService.save(tagDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(save);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagDto> update(@PathVariable Integer id, @RequestBody TagDto tagDto) {
        TagDto update = tagService.update(id, tagDto);
        System.out.println("tagDto: " + update);
        return ResponseEntity.ok(update);
    }

}
