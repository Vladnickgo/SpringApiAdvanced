package com.epam.esm.SpringApiAdvanced.service;

import com.epam.esm.SpringApiAdvanced.service.dto.GiftCertificateDto;

import java.util.List;

public interface GiftCertificateService {
    GiftCertificateDto save(GiftCertificateDto giftCertificateDto);

    List<GiftCertificateDto> findAll();
}
