package com.epam.esm.SpringApiAdvanced.repository;

import com.epam.esm.SpringApiAdvanced.repository.entity.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
    List<Tag> findAll();

    Tag findByName(String tagName);

    List<Tag> findByNameContainingIgnoreCase(String pertOfName, Pageable pageable);

    @Modifying
    @Query("update Tag t set t.name = :name where t.id = :id")
    void updateTagById(@Param("id") Integer id, @Param("name") String name);
}

