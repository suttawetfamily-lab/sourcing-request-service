package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantMasterGridField;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantMasterGridField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantMasterGridFieldRepository extends JpaRepository<TenantMasterGridField, Integer> {

    List<TenantMasterGridField> findByGroupNameAndTenant_CodeOrderBySequence(String groupName, String tenantCode);

    List<TenantMasterGridField> findByGroupNameAndTenant_CodeAndSearchableOrderBySequence(
            String groupName, String tenantCode, boolean searchable);
    List<TenantMasterGridField> findByTenant_CodeAndSearchableOrderBySequence(String tenantCode, boolean searchable);
}
