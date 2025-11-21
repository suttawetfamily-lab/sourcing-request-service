package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface ProjectChangeLogDetailRepository extends JpaRepository<ProjectChangeLogDetail, Long> {
    @Modifying
    @Query(value = "DELETE pcld FROM ProjectChangeLogDetail pcld " +
                   "INNER JOIN ProjectChangeLogHeader pclh ON pcld.ChangeLogHeaderId = pclh.RecId " +
                   "WHERE pclh.TenantId = :tenantId ", nativeQuery = true)
    void deleteByTenantId(@Param("tenantId") Integer tenantId);

    @Query(value = "SELECT pcld.* FROM ProjectChangeLogDetail pcld " +
            "INNER JOIN ProjectChangeLogHeader pclh ON pcld.ChangeLogHeaderId = pclh.RecId " +
            "WHERE pclh.TenantId = :tenantId ", nativeQuery = true)
    List<ProjectChangeLogDetail> findByTenantId(@Param("tenantId") Integer tenantId);
}
