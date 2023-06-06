package com.epam.esm.SpringApiAdvanced.repository;

import com.epam.esm.SpringApiAdvanced.repository.entity.GiftCertificate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface GiftCertificateRepository extends CrudDao<GiftCertificate, Integer> {

    Page<GiftCertificate> findBySeveralTags(Set<String> namesSet, Pageable pageable);

    Integer findLastAddedId();
}
