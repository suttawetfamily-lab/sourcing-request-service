package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExcSourcingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExcSourcingStatusRepository extends JpaRepository<ExcSourcingStatus, Integer> {

    // หา status ตาม code
    Optional<ExcSourcingStatus> findByCode(String code);

    // หา status ตาม description
    Optional<ExcSourcingStatus> findByDescription(String description);
}
