package com.epam.esm.SpringApiAdvanced.service.impl;

import com.epam.esm.SpringApiAdvanced.exception.DataBaseRuntimeException;
import com.epam.esm.SpringApiAdvanced.exception.NotFoundException;
import com.epam.esm.SpringApiAdvanced.repository.entity.Tag;
import com.epam.esm.SpringApiAdvanced.repository.impl.TagRepositoryImpl;
import com.epam.esm.SpringApiAdvanced.service.TagService;
import com.epam.esm.SpringApiAdvanced.service.dto.TagDto;
import com.epam.esm.SpringApiAdvanced.service.mapper.TagMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    private final TagRepositoryImpl tagRepository;
    private final TagMapper tagMapper;

    @Autowired
    public TagServiceImpl(TagRepositoryImpl tagRepository, TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
    }

    @Override
    public TagDto findById(Integer id) {
        return tagRepository.findById(id)
                .map(tagMapper::mapEntityToDto)
                .orElseThrow(() -> new NotFoundException("Tag resource not found (id = " + id + ")"));
    }

    @Override
    @Transactional
    public TagDto save(TagDto tagDto) {
        try {
            Tag tag = tagMapper.mapDtoToEntity(tagDto);
            tagRepository.save(tag);
            Tag byName = tagRepository.findByName(tag.getName());
            return tagMapper.mapEntityToDto(byName);
        } catch (DataIntegrityViolationException exception) {
            throw new DataBaseRuntimeException("Tag with name=" + tagDto.getName() + " already exist");
        }
    }

    @Override
    public Page<TagDto> findAll(Pageable pageable) {
        Page<Tag> page = tagRepository.findAll(pageable);
        List<TagDto> collect = page.stream().map(tagMapper::mapEntityToDto).toList();
        return new PageImpl<>(collect, pageable, countAll());
    }

    private Integer countAll() {
        return tagRepository.countAll();
    }

    @Override
    public Page<TagDto> findAllByTagName(String tagName, Pageable pageable) {
        List<TagDto> collect = tagRepository.findByNameContainingIgnoreCase(tagName, pageable).stream()
                .map(tagMapper::mapEntityToDto)
                .toList();
        return new PageImpl<>(collect, pageable, collect.size());
    }

    @Override
    @Transactional
    public TagDto update(Integer id, TagDto tagDto) {
        tagRepository.update(id, tagMapper.mapDtoToEntity(tagDto));
        return tagRepository.findById(id)
                .map(tagMapper::mapEntityToDto)
                .orElseThrow(() -> new DataBaseRuntimeException("Entity not update"));
    }

    @Override
    public TagDto findMostWidelyUsedTag() {
        Tag mostWiselyUsedTag = tagRepository.findMostWidelyUsedTag();
        return tagMapper.mapEntityToDto(mostWiselyUsedTag);
    }


}
