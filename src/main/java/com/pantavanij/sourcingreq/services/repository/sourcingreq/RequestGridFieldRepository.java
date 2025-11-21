package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestGridField;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantRequestGridField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequestGridFieldRepository extends JpaRepository<RequestGridField, Integer> {
    Optional<RequestGridField> findByCode(String code);
}
