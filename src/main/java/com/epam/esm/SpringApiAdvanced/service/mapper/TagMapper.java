package com.epam.esm.SpringApiAdvanced.service.mapper;

import com.epam.esm.SpringApiAdvanced.repository.entity.Tag;
import com.epam.esm.SpringApiAdvanced.service.dto.TagDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagMapper {
    Tag mapDtoToEntity(TagDto tagDto);

    TagDto mapEntityToDto(Tag tag);
}
