package com.epam.esm.SpringApiAdvanced.repository;

import com.epam.esm.SpringApiAdvanced.repository.entity.GiftCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftCertificateRepository extends JpaRepository<GiftCertificate, Integer> {

}
