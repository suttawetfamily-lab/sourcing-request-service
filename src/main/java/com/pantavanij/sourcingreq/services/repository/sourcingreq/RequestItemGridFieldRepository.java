package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItemGridField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequestItemGridFieldRepository extends JpaRepository<RequestItemGridField, Integer> {
    Optional<RequestItemGridField> findByCode(String code);
}
