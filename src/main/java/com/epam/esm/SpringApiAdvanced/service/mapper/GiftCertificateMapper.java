package com.epam.esm.SpringApiAdvanced.service.mapper;

import com.epam.esm.SpringApiAdvanced.repository.entity.GiftCertificate;
import com.epam.esm.SpringApiAdvanced.service.dto.GiftCertificateDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GiftCertificateMapper {

    GiftCertificate mapDtoToEntity(GiftCertificateDto giftCertificateDto);

    GiftCertificateDto mapEntityToDto(GiftCertificate giftCertificate);
}
