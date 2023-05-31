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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private static final List<GiftCertificateDto> EXPECTED_GIFT_CERTIFICATE_DTO_LIST = new ArrayList<>(List.of(
            GiftCertificateDto.builder()
                    .id(1)
                    .name("Alpha")
                    .price(19)
                    .description("Crete")
                    .duration(54)
                    .createDate(LocalDate.of(2021, 7, 16))
                    .lastUpdateDate(LocalDate.of(2025, 8, 1))
                    .build(),
            GiftCertificateDto.builder()
                    .id(2)
                    .name("Beta")
                    .price(45)
                    .description("Prime")
                    .duration(61)
                    .createDate(LocalDate.of(2021, 4, 15))
                    .lastUpdateDate(LocalDate.of(2025, 12, 15))
                    .build(),
            GiftCertificateDto.builder()
                    .id(3)
                    .name("Gamma")
                    .price(10)
                    .description("Jupiter")
                    .duration(52)
                    .createDate(LocalDate.of(2022, 5, 10))
                    .lastUpdateDate(LocalDate.of(2024, 6, 1))
                    .build()));
    private static final Optional<GiftCertificate> TEST_OPTIONAL_ENTITY = Optional.of(
            GiftCertificate.builder()
                    .id(1)
                    .name("Alpha")
                    .price(19)
                    .description("Crete")
                    .duration(54)
                    .createDate(LocalDate.of(2021, 7, 16))
                    .lastUpdateDate(LocalDate.of(2025, 8, 1))
                    .build());

    private static final GiftCertificate TEST_ENTITY = GiftCertificate.builder()
            .id(1)
            .name("Alpha")
            .price(19)
            .description("Crete")
            .duration(54)
            .createDate(LocalDate.of(2021, 7, 16))
            .lastUpdateDate(LocalDate.of(2025, 8, 1))
            .build();
    private static final GiftCertificateDto EXPECTED_GIFT_CERTIFICATE_DTO =
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
        GiftCertificateDto giftCertificateDto = EXPECTED_GIFT_CERTIFICATE_DTO;
        GiftCertificate giftCertificate = TEST_ENTITY;
        when(giftCertificateMapper.mapDtoToEntity(giftCertificateDto)).thenReturn(giftCertificate);
        GiftCertificateDto result = giftCertificateService.save(giftCertificateDto);
        verify(giftCertificateRepository).save(giftCertificate);
        assertEquals(giftCertificateDto, result);
    }

    @Test
    void testFindAll() {

    }

    @Test
    void findById() {
    }

    @Test
    void update() {
    }

    @Test
    void findBySeveralTags() {
    }
}