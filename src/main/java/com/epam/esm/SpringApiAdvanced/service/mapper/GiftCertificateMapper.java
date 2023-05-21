package com.epam.esm.SpringApiAdvanced.service.mapper;

import com.epam.esm.SpringApiAdvanced.repository.entity.GiftCertificate;
import com.epam.esm.SpringApiAdvanced.service.dto.GiftCertificateDto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface GiftCertificateMapper {
@Mappings({
        @Mapping(target = "certificateId", source = "id"),
        @Mapping(target = "name", source = "name"),
        @Mapping(target = "description", source = "description"),
        @Mapping(target = "price", source = "price"),
        @Mapping(target = "duration", source = "price"),
        @Mapping(target = "createDate", source = "createDate"),
        @Mapping(target = "lastUpdateDate", source = "lastUpdateDate")
})
    GiftCertificate mapDtoToEntity(GiftCertificateDto giftCertificateDto);
    @InheritInverseConfiguration
    GiftCertificateDto mapEntityToDto(GiftCertificate giftCertificate);
}
