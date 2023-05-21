package com.epam.esm.SpringApiAdvanced.controller;

import com.epam.esm.SpringApiAdvanced.service.GiftCertificateService;
import com.epam.esm.SpringApiAdvanced.service.dto.GiftCertificateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/")
    public List<GiftCertificateDto> findAll() {
        return giftCertificateService.findAll();
    }

    @PostMapping("/")
    public GiftCertificateDto save(@RequestBody GiftCertificateDto giftCertificateDto) {
        giftCertificateService.save(giftCertificateDto);
        return giftCertificateDto;
    }
}
