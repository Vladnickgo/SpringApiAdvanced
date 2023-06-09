package com.epam.esm.SpringApiAdvanced.service.impl;

import com.epam.esm.SpringApiAdvanced.exception.DataBaseRuntimeException;
import com.epam.esm.SpringApiAdvanced.exception.NotFoundException;
import com.epam.esm.SpringApiAdvanced.repository.entity.Tag;
import com.epam.esm.SpringApiAdvanced.repository.impl.TagRepositoryImpl;
import com.epam.esm.SpringApiAdvanced.service.dto.TagDto;
import com.epam.esm.SpringApiAdvanced.service.mapper.TagMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {
    @Mock
    TagRepositoryImpl tagRepository;
    @Mock
    TagMapper tagMapper;
    @InjectMocks
    TagServiceImpl tagService;

    @Test
    void testFindById() {
        Tag tag = Tag.builder()
                .id(1)
                .name("Tag name")
                .build();
        TagDto tagDto = TagDto.builder()
                .id(1)
                .name("Tag name")
                .build();
        when(tagRepository.findById(1)).thenReturn(Optional.ofNullable(tag));
        when(tagMapper.mapEntityToDto(tag)).thenReturn(tagDto);
        TagDto byId = tagService.findById(1);
        verify(tagRepository, times(1)).findById(1);
        assertEquals(tagDto, byId);
    }

    @Test
    void testFindByIdIfNullable() {
        String expectedMessage = "Tag resource not found (id = 1)";
        when(tagRepository.findById(1)).thenReturn(Optional.empty());
        String message = assertThrows(NotFoundException.class, () -> tagService.findById(1)).getMessage();
        assertEquals(expectedMessage, message);
    }


    @Test
    void testSave() {
        Tag tag = Tag.builder()
                .id(1)
                .name("Tag name")
                .build();
        TagDto tagDto = TagDto.builder()
                .id(1)
                .name("Tag name")
                .build();
        when(tagMapper.mapDtoToEntity(tagDto)).thenReturn(tag);
        doNothing().when(tagRepository).save(tag);
        when(tagRepository.findByName(tag.getName())).thenReturn(tag);
        when(tagMapper.mapEntityToDto(tag)).thenReturn(tagDto);
        TagDto save = tagService.save(tagDto);
        assertEquals(tagDto, save);
        verify(tagRepository, times(1)).save(tag);
    }

    @Test
    void testSaveFailed() {
        String expectedMessage = "Tag with name=Tag name already exist";
        TagDto tagDto = TagDto.builder()
                .id(1)
                .name("Tag name")
                .build();
        Tag tag = Tag.builder()
                .id(1)
                .name("Tag name")
                .build();
        when(tagMapper.mapDtoToEntity(tagDto)).thenReturn(tag);
        doThrow(new DataIntegrityViolationException("")).when(tagRepository).save(tag);
        String message = assertThrows(DataBaseRuntimeException.class, () -> tagService.save(tagDto)).getMessage();
        assertEquals(expectedMessage, message);
    }

    @Test
    void testFindAll() {
        Tag tag = Tag.builder()
                .id(1)
                .name("Tag name")
                .build();
        TagDto tagDto = TagDto.builder()
                .id(1)
                .name("Tag name")
                .build();
        Pageable pageable = PageRequest.of(0, 10);
        List<Tag> tagList = List.of(tag);
        PageImpl<Tag> orderPage = new PageImpl<>(tagList, pageable, 1);
        when(tagRepository.findAll(pageable)).thenReturn(orderPage);
        when(tagMapper.mapEntityToDto(tag)).thenReturn(tagDto);
        when(tagRepository.countAll()).thenReturn(1);
        Page<TagDto> all = tagService.findAll(pageable);
        assertEquals(1, all.getTotalElements());
        assertEquals(1, all.getTotalPages());
    }

    @Test
    void findAllByTagName() {
        Tag tag = Tag.builder()
                .id(1)
                .name("Tag name")
                .build();
        TagDto tagDto = TagDto.builder()
                .id(1)
                .name("Tag name")
                .build();
        Pageable pageable = PageRequest.of(0, 10);
        List<Tag> tagList = List.of(tag);
        when(tagRepository.findByNameContainingIgnoreCase(tag.getName(), pageable)).thenReturn(tagList);
        when(tagMapper.mapEntityToDto(tag)).thenReturn(tagDto);
        Page<TagDto> allByTagName = tagService.findAllByTagName(tag.getName(), pageable);
        assertEquals(1, allByTagName.getTotalElements());
        assertEquals(1, allByTagName.getTotalPages());
    }

    @Test
    void testUpdate() {
        Tag tag = Tag.builder()
                .id(1)
                .name("Tag name")
                .build();
        TagDto tagDto = TagDto.builder()
                .id(1)
                .name("Tag name")
                .build();
        when(tagMapper.mapDtoToEntity(tagDto)).thenReturn(tag);
        when(tagMapper.mapEntityToDto(tag)).thenReturn(tagDto);
        when(tagRepository.findById(1)).thenReturn(Optional.ofNullable(tag));
        doNothing().when(tagRepository).update(1, tag);
        TagDto update = tagService.update(1, tagDto);
        assertEquals(tagDto, update);
        verify(tagMapper, times(1)).mapDtoToEntity(tagDto);
        verify(tagMapper, times(1)).mapEntityToDto(tag);
        verify(tagRepository).findById(1);
    }

    @Test
    void testUpdateForException() {
        String expectedMessage = "Entity not update";
        Tag tag = Tag.builder()
                .id(1)
                .name("Tag name")
                .build();
        TagDto tagDto = TagDto.builder()
                .id(1)
                .name("Tag name")
                .build();
        when(tagMapper.mapDtoToEntity(tagDto)).thenReturn(tag);
        when(tagRepository.findById(1)).thenReturn(Optional.empty());
        String message = assertThrows(DataBaseRuntimeException.class, () -> tagService.update(1, tagDto)).getMessage();
        assertEquals(expectedMessage, message);
    }

    @Test
    void findMostWidelyUsedTag() {
        Tag tag = Tag.builder()
                .id(1)
                .name("Tag name")
                .build();
        TagDto tagDto = TagDto.builder()
                .id(1)
                .name("Tag name")
                .build();
        when(tagRepository.findMostWidelyUsedTag()).thenReturn(tag);
        when(tagMapper.mapEntityToDto(tag)).thenReturn(tagDto);
        TagDto mostWidelyUsedTag = tagService.findMostWidelyUsedTag();
        assertEquals(tagDto,mostWidelyUsedTag);
    }
}