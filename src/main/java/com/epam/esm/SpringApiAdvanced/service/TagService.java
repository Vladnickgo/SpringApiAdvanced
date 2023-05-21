package com.epam.esm.SpringApiAdvanced.service;

import com.epam.esm.SpringApiAdvanced.service.dto.TagDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TagService {
    TagDto findById(Integer id);

    TagDto save(TagDto tagDto);

    Page<TagDto> findAll(Pageable pageable);

    void deleteById(String id);

    Page<TagDto> findAllByTagName(String tagName, Pageable pageable);

    TagDto update(Integer id, TagDto tagDto);
}
