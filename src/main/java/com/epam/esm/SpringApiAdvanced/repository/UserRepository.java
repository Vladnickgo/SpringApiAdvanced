package com.epam.esm.SpringApiAdvanced.repository;

import com.epam.esm.SpringApiAdvanced.repository.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserRepository extends CrudDao<User, Integer> {
    Page<User> findByLastNameContainsIgnoreCase(String name, Pageable pageable);
}
