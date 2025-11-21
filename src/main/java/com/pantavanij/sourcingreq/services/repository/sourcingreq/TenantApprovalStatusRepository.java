package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantApprovalStatusRepository extends JpaRepository<TenantApprovalStatus, Integer> {

    List<TenantApprovalStatus> findByTenant_CodeAndNameNotIn(String tenantCode, List<String> excludedStatus);

    @Query(value = "SELECT * FROM TenantApprovalStatus WHERE TenantId = :tenantId AND Name IN :name", nativeQuery = true)
    List<TenantApprovalStatus> findByTenantIdAndNameIn(@Param("tenantId") Integer tenantId,
                                                       @Param("name") List<String> name);

    TenantApprovalStatus findByNameAndTenant(String name, Tenant tenant);
}
