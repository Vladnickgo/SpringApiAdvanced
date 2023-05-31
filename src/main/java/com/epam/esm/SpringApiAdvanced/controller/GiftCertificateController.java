package com.epam.esm.SpringApiAdvanced.controller;

import com.epam.esm.SpringApiAdvanced.service.GiftCertificateService;
import com.epam.esm.SpringApiAdvanced.service.dto.GiftCertificateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@ResponseBody
@RequestMapping("/certificate")
@EnableHypermediaSupport(type = {EnableHypermediaSupport.HypermediaType.HAL})
public class GiftCertificateController {
    private final GiftCertificateService giftCertificateService;

    @Autowired
    public GiftCertificateController(GiftCertificateService giftCertificateService) {
        this.giftCertificateService = giftCertificateService;
    }

    @GetMapping("")
    public ResponseEntity<PagedModel<GiftCertificateDto>> findAll(Pageable pageable) {
        Page<GiftCertificateDto> certificates = giftCertificateService.findAll(pageable);
        List<Link> certificateLinks = certificates.stream()
                .map(t -> linkTo(methodOn(GiftCertificateController.class).findById(t.getId())).withSelfRel())
                .toList();
        return getPagedModelResponseEntity(pageable, certificates, certificateLinks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<GiftCertificateDto>> findById(@PathVariable Integer id) {
        GiftCertificateDto giftCertificateDto = giftCertificateService.findById(id);
        String uriString = ServletUriComponentsBuilder.fromCurrentRequest()
                .toUriString();
        Link selfLink = Link.of(uriString);
        EntityModel<GiftCertificateDto> entityModel = EntityModel.of(giftCertificateDto, selfLink);
        return ResponseEntity.ok(entityModel);
    }

    @PostMapping("/")
    public GiftCertificateDto save(@RequestBody GiftCertificateDto giftCertificateDto) {
        giftCertificateService.save(giftCertificateDto);
        return giftCertificateDto;
    }

    private ResponseEntity<PagedModel<GiftCertificateDto>> getPagedModelResponseEntity(@PageableDefault(page = 1, sort = "name") Pageable pageable, Page<GiftCertificateDto> all, List<Link> certificateLinks) {
        Link selfLink = linkTo(methodOn(GiftCertificateController.class).findAll(pageable)).withSelfRel();
        PagedModel<GiftCertificateDto> pagedModel = PagedModel.of(all.getContent(), new PagedModel.PageMetadata(all.getSize(), all.getNumber(), all.getTotalElements()), certificateLinks);
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

    @PutMapping("/update/{id}")
    public ResponseEntity<EntityModel<GiftCertificateDto>> update(@RequestBody GiftCertificateDto giftCertificateDto, @PathVariable Integer id) {
        GiftCertificateDto update = giftCertificateService.update(id, giftCertificateDto);
        UriComponents uriComponents = ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath("/certificate/{id}")
                .buildAndExpand(id);
        Link selfLink = Link.of(uriComponents.toUriString());
        EntityModel<GiftCertificateDto> entityModel = EntityModel.of(update, selfLink);
        return ResponseEntity.ok(entityModel);
    }

    @GetMapping("/tags")
    public ResponseEntity<PagedModel<GiftCertificateDto>> findBySeveralTags(@RequestParam String name, @PageableDefault(size = 5) Pageable pageable) {
        Page<GiftCertificateDto> bySeveralTags = giftCertificateService.findBySeveralTags(name, pageable);
        List<Link> certificateLinks = bySeveralTags.stream()
                .map(t -> linkTo(methodOn(GiftCertificateController.class).findById(t.getId())).withRel("find_by_id"))
                .toList();
        Link findAllLink = linkTo(methodOn(GiftCertificateController.class).findAll(pageable)).withSelfRel().withRel("find_all");
        String uriString = ServletUriComponentsBuilder.fromCurrentRequest().toUriString();
        Link selfLink = Link.of(uriString, "self_link");
        PagedModel<GiftCertificateDto> pagedModel = PagedModel.of(bySeveralTags.getContent(), new PagedModel.PageMetadata(bySeveralTags.getSize(), bySeveralTags.getNumber(), bySeveralTags.getTotalElements()), certificateLinks);
        pagedModel.add(findAllLink);
        pagedModel.add(selfLink);
        if (bySeveralTags.hasPrevious()) {
            String prevLink = ServletUriComponentsBuilder.fromCurrentRequest()
                    .replaceQueryParam("page", bySeveralTags.getNumber() - 1)
                    .replaceQueryParam("size", bySeveralTags.getSize())
                    .toUriString();
            pagedModel.add(Link.of(prevLink, "prev"));
        }
        if (bySeveralTags.hasNext()) {
            String nextLink = ServletUriComponentsBuilder.fromCurrentRequest()
                    .replaceQueryParam("page", bySeveralTags.getNumber() + 1)
                    .replaceQueryParam("size", bySeveralTags.getSize())
                    .toUriString();
            pagedModel.add(Link.of(nextLink, "next"));
        }
        return ResponseEntity.ok(pagedModel);
    }
}
