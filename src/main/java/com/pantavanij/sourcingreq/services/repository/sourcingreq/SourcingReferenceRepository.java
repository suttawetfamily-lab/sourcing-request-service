package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SourcingReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SourcingReferenceRepository extends JpaRepository<SourcingReference, Long> {

    SourcingReference findByTenantId(Integer tenantId);

    SourcingReference findByTypeAndTenantIdAndTemplateId(String type, Integer tenantId, Integer templateId);

    List<SourcingReference> findAllByTypeAndTenantId(String type, Integer tenantId);


}
