package com.epam.esm.SpringApiAdvanced.service.mapper;

import com.epam.esm.SpringApiAdvanced.repository.entity.Tag;
import com.epam.esm.SpringApiAdvanced.service.dto.TagDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TagMapper {
    @Mapping(target = "id", source = "id")
    Tag mapDtoToEntity(TagDto tagDto);

    @Mapping(target = "id", source = "id")
    TagDto mapEntityToDto(Tag tag);
}
