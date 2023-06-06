package com.epam.esm.SpringApiAdvanced.service.impl;

import com.epam.esm.SpringApiAdvanced.repository.entity.GiftCertificate;
import com.epam.esm.SpringApiAdvanced.repository.impl.GiftCertificateRepositoryImpl;
import com.epam.esm.SpringApiAdvanced.service.dto.GiftCertificateDto;
import com.epam.esm.SpringApiAdvanced.service.mapper.GiftCertificateMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GiftCertificateServiceImplTest {
    private static final List<GiftCertificate> ENTITY_LIST = new ArrayList<>(List.of(
            GiftCertificate.builder()
                    .id(1)
                    .name("Alpha")
                    .price(19)
                    .description("Crete")
                    .duration(54)
                    .createDate(LocalDate.of(2021, 7, 16))
                    .lastUpdateDate(LocalDate.of(2025, 8, 1))
                    .build(),
            GiftCertificate.builder()
                    .id(2)
                    .name("Beta")
                    .price(45)
                    .description("Prime")
                    .duration(61)
                    .createDate(LocalDate.of(2021, 4, 15))
                    .lastUpdateDate(LocalDate.of(2025, 12, 15))
                    .build(),
            GiftCertificate.builder()
                    .id(3)
                    .name("Gamma")
                    .price(10)
                    .description("Jupiter")
                    .duration(52)
                    .createDate(LocalDate.of(2022, 5, 10))
                    .lastUpdateDate(LocalDate.of(2024, 6, 1))
                    .build()));
        private static final GiftCertificate TEST_ENTITY = GiftCertificate.builder()
            .id(1)
            .name("Alpha")
            .price(19)
            .description("Crete")
            .duration(54)
            .createDate(LocalDate.of(2021, 7, 16))
            .lastUpdateDate(LocalDate.of(2025, 8, 1))
            .build();
    private static final GiftCertificateDto GIFT_CERTIFICATE_DTO =
            GiftCertificateDto.builder()
                    .id(1)
                    .name("Alpha")
                    .price(19)
                    .description("Crete")
                    .duration(54)
                    .createDate(LocalDate.of(2021, 7, 16))
                    .lastUpdateDate(LocalDate.of(2025, 8, 1))
                    .build();

    @Mock
    private GiftCertificateRepositoryImpl giftCertificateRepository;
    @Mock
    private GiftCertificateMapper giftCertificateMapper;
    @InjectMocks
    private GiftCertificateServiceImpl giftCertificateService;

    @Test
    void testSave() {
        when(giftCertificateMapper.mapDtoToEntity(GIFT_CERTIFICATE_DTO)).thenReturn(TEST_ENTITY);
        when(giftCertificateMapper.mapEntityToDto(TEST_ENTITY)).thenReturn(GIFT_CERTIFICATE_DTO);
        when(giftCertificateRepository.findLastAddedId()).thenReturn(1);
        when(giftCertificateRepository.findById(1)).thenReturn(Optional.of(TEST_ENTITY));
        doNothing().when(giftCertificateRepository).save(any(GiftCertificate.class));
        GiftCertificateDto actual = giftCertificateService.save(GIFT_CERTIFICATE_DTO);
        verify(giftCertificateRepository, times(1)).save(any(GiftCertificate.class));
        assertEquals(GIFT_CERTIFICATE_DTO, actual);
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<GiftCertificate> expectedPage = new PageImpl<>(ENTITY_LIST, pageable, ENTITY_LIST.size());
        when(giftCertificateRepository.findAll(pageable)).thenReturn(expectedPage);
        when(giftCertificateMapper.mapEntityToDto(any(GiftCertificate.class))).thenAnswer(invocation -> {
            GiftCertificate giftCertificate = invocation.getArgument(0);
            return new GiftCertificateDto(giftCertificate.getId(), giftCertificate.getName(), giftCertificate.getDescription(), giftCertificate.getPrice(), giftCertificate.getDuration(), giftCertificate.getCreateDate(), giftCertificate.getLastUpdateDate());
        });
        Page<GiftCertificateDto> result = giftCertificateService.findAll(pageable);
        assertEquals(3, result.getContent().size());
        assertEquals(3, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(0, result.getNumber());
        assertFalse(result.hasNext());
        assertFalse(result.hasPrevious());
    }

    @Test
    void testFindById() {
        when(giftCertificateRepository.findById(1)).thenReturn(Optional.of(TEST_ENTITY));
        when(giftCertificateMapper.mapEntityToDto(TEST_ENTITY)).thenReturn(GIFT_CERTIFICATE_DTO);
        GiftCertificateDto actual = giftCertificateService.findById(1);
        assertNotNull(actual);
        assertEquals(GIFT_CERTIFICATE_DTO, actual);
    }


    @Test
    void testFindBySeveralTags() {
        Pageable pageable = PageRequest.of(0, 10);
        List<GiftCertificate> certificateList = List.of(TEST_ENTITY);
        when(giftCertificateRepository.findBySeveralTags(anySet(), eq(pageable))).thenReturn(new PageImpl<>(certificateList, pageable, ENTITY_LIST.size()));
        when(giftCertificateRepository.countCertificatesBySeveralTags(anySet())).thenReturn(1);
        when(giftCertificateMapper.mapEntityToDto(TEST_ENTITY)).thenReturn(GIFT_CERTIFICATE_DTO);
        Page<GiftCertificateDto> result = giftCertificateService.findBySeveralTags("tag1, tag2", pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testUpdate() {
        Integer id = 1;
        GiftCertificateDto giftCertificateDto = GiftCertificateDto.builder()
                .name("New name")
                .build();
        GiftCertificate newGiftCertificate = GiftCertificate.builder()
                .id(1)
                .name("New name")
                .price(19)
                .description("Crete")
                .duration(54)
                .createDate(LocalDate.of(2021, 7, 16))
                .lastUpdateDate(LocalDate.of(2025, 8, 1))
                .build();
        GiftCertificate oldGiftCertificate = GiftCertificate.builder()
                .id(1)
                .name("Old name")
                .price(19)
                .description("Crete")
                .duration(54)
                .createDate(LocalDate.of(2021, 7, 16))
                .lastUpdateDate(LocalDate.of(2025, 8, 1))
                .build();

        when(giftCertificateMapper.mapDtoToEntity(any(GiftCertificateDto.class))).thenReturn(newGiftCertificate, oldGiftCertificate);
        when(giftCertificateMapper.mapEntityToDto(any(GiftCertificate.class))).thenReturn(giftCertificateDto);
        when(giftCertificateRepository.findById(1)).thenReturn(Optional.of(oldGiftCertificate));
        doNothing().when(giftCertificateRepository).update(any(Integer.class),any(GiftCertificate.class));
        giftCertificateService.update(1,GIFT_CERTIFICATE_DTO);
        verify(giftCertificateMapper, times(3)).mapDtoToEntity(any(GiftCertificateDto.class));
        verify(giftCertificateMapper, times(2)).mapEntityToDto(any(GiftCertificate.class));
        verify(giftCertificateRepository, times(2)).findById(id);
        verify(giftCertificateRepository).update(eq(id), any(GiftCertificate.class));
    }
}

