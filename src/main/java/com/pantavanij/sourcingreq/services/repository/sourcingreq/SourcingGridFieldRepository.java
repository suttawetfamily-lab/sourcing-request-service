package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SourcingGridField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SourcingGridFieldRepository extends JpaRepository<SourcingGridField, Integer> {
    Optional<SourcingGridField> findByCode(String code);
}
