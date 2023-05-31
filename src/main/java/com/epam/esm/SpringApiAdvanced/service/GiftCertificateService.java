package com.epam.esm.SpringApiAdvanced.service;

import com.epam.esm.SpringApiAdvanced.service.dto.GiftCertificateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GiftCertificateService {
    GiftCertificateDto save(GiftCertificateDto giftCertificateDto);

    Page<GiftCertificateDto> findAll(Pageable pageable);

    GiftCertificateDto findById(int certificateId);

    GiftCertificateDto update(Integer id, GiftCertificateDto giftCertificateDto);

    Page<GiftCertificateDto> findBySeveralTags(String name, Pageable pageable);
}
