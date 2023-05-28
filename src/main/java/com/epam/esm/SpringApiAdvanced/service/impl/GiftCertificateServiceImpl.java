package com.epam.esm.SpringApiAdvanced.service.impl;

import com.epam.esm.SpringApiAdvanced.exception.NotFoundException;
import com.epam.esm.SpringApiAdvanced.repository.GiftCertificateRepository;
import com.epam.esm.SpringApiAdvanced.repository.entity.GiftCertificate;
import com.epam.esm.SpringApiAdvanced.service.GiftCertificateService;
import com.epam.esm.SpringApiAdvanced.service.dto.GiftCertificateDto;
import com.epam.esm.SpringApiAdvanced.service.mapper.GiftCertificateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GiftCertificateServiceImpl implements GiftCertificateService {
    private final GiftCertificateRepository giftCertificateRepository;
    private final GiftCertificateMapper giftCertificateMapper;

    @Autowired
    public GiftCertificateServiceImpl(GiftCertificateRepository giftCertificateRepository, GiftCertificateMapper giftCertificateMapper) {
        this.giftCertificateRepository = giftCertificateRepository;
        this.giftCertificateMapper = giftCertificateMapper;
    }

    @Override
    public GiftCertificateDto save(GiftCertificateDto giftCertificateDto) {
        GiftCertificate giftCertificate = giftCertificateMapper.mapDtoToEntity(giftCertificateDto);
        giftCertificateRepository.save(giftCertificate);
        return giftCertificateDto;
    }

    @Override
    public List<GiftCertificateDto> findAll() {
        return giftCertificateRepository.findAll().stream()
                .map(giftCertificateMapper::mapEntityToDto)
                .toList();
    }

    @Override
    public GiftCertificateDto findById(int certificateId) {
        return giftCertificateRepository.findById(certificateId)
                .map(giftCertificateMapper::mapEntityToDto)
                .orElseThrow(() -> new NotFoundException("Certificate with id=" + certificateId + " not found"));
    }
}
