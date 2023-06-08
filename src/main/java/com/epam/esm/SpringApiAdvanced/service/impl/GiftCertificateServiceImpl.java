package com.epam.esm.SpringApiAdvanced.service.impl;

import com.epam.esm.SpringApiAdvanced.exception.NotFoundException;
import com.epam.esm.SpringApiAdvanced.repository.entity.GiftCertificate;
import com.epam.esm.SpringApiAdvanced.repository.impl.GiftCertificateRepositoryImpl;
import com.epam.esm.SpringApiAdvanced.service.GiftCertificateService;
import com.epam.esm.SpringApiAdvanced.service.dto.GiftCertificateDto;
import com.epam.esm.SpringApiAdvanced.service.mapper.GiftCertificateMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GiftCertificateServiceImpl implements GiftCertificateService {
    private final GiftCertificateRepositoryImpl giftCertificateRepository;
    private final GiftCertificateMapper giftCertificateMapper;

    @Autowired
    public GiftCertificateServiceImpl(GiftCertificateRepositoryImpl giftCertificateRepository, GiftCertificateMapper giftCertificateMapper) {
        this.giftCertificateRepository = giftCertificateRepository;
        this.giftCertificateMapper = giftCertificateMapper;
    }

    @Override
    @Transactional
    public GiftCertificateDto save(GiftCertificateDto giftCertificateDto) {
        GiftCertificate giftCertificate = giftCertificateMapper.mapDtoToEntity(giftCertificateDto);
        giftCertificateRepository.save(giftCertificate);
        Integer lastAddedId = giftCertificateRepository.findLastAddedId();
        GiftCertificate responseCertificate = giftCertificateRepository.findById(lastAddedId)
                .orElseThrow(() -> new NotFoundException("Certificate with id=" + lastAddedId + " not found"));
        return giftCertificateMapper.mapEntityToDto(responseCertificate);
    }

    @Override
    public Page<GiftCertificateDto> findAll(Pageable pageable) {
        List<GiftCertificateDto> dtoList = giftCertificateRepository.findAll(pageable).stream()
                .map(giftCertificateMapper::mapEntityToDto)
                .toList();
        return new PageImpl<>(dtoList, pageable, countAll());
    }

    private Integer countAll() {
        return giftCertificateRepository.countAll();
    }

    @Override
    public GiftCertificateDto findById(int certificateId) {
        return giftCertificateRepository.findById(certificateId)
                .map(giftCertificateMapper::mapEntityToDto)
                .orElseThrow(() -> new NotFoundException("Certificate with id=" + certificateId + " not found"));
    }

    @Override
    public Page<GiftCertificateDto> findBySeveralTags(String name, Pageable pageable) {
        String[] namesArray = name.split(",");
        Set<String> namesSet = Arrays.stream(namesArray).map(String::trim).collect(Collectors.toSet());
        List<GiftCertificateDto> certificateDtoList = giftCertificateRepository.findBySeveralTags(namesSet, pageable).stream().map(giftCertificateMapper::mapEntityToDto).toList();
        return new PageImpl<>(certificateDtoList, pageable, giftCertificateRepository.countCertificatesBySeveralTags(namesSet));
    }

    @Transactional
    @Override
    public GiftCertificateDto update(Integer id, GiftCertificateDto giftCertificateDto) {
        GiftCertificateDto newGiftCertificateDto = getNewGiftCertificateDto(id, giftCertificateDto);
        GiftCertificate giftCertificate = giftCertificateMapper.mapDtoToEntity(newGiftCertificateDto);
        giftCertificateRepository.update(id, giftCertificate);
        return findById(id);
    }

    private GiftCertificateDto getNewGiftCertificateDto(Integer id, GiftCertificateDto giftCertificateDto) {
        GiftCertificate newGiftCertificate = giftCertificateMapper.mapDtoToEntity(giftCertificateDto);
        GiftCertificate oldGiftCertificate = giftCertificateMapper.mapDtoToEntity(findById(id));
        return GiftCertificateDto.builder()
                .id(id)
                .createDate(newGiftCertificate.getCreateDate() == null ? oldGiftCertificate.getCreateDate() : newGiftCertificate.getCreateDate())
                .description(newGiftCertificate.getDescription() == null ? oldGiftCertificate.getDescription() : newGiftCertificate.getDescription())
                .duration(newGiftCertificate.getDuration() == null ? oldGiftCertificate.getDuration() : newGiftCertificate.getDuration())
                .lastUpdateDate(newGiftCertificate.getLastUpdateDate() == null ? oldGiftCertificate.getLastUpdateDate() : newGiftCertificate.getLastUpdateDate())
                .name(newGiftCertificate.getName() == null ? oldGiftCertificate.getName() : newGiftCertificate.getName())
                .price(newGiftCertificate.getPrice() == null ? oldGiftCertificate.getPrice() : newGiftCertificate.getPrice())
                .build();
    }
}
