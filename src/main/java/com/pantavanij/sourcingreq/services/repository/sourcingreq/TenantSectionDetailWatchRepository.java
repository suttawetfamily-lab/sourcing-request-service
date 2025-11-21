package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;

@Repository
public interface TenantSectionDetailWatchRepository extends JpaRepository<TenantSectionDetailWatch, Integer> {
    @Modifying
    @Query(value = "DELETE FROM TenantSectionDetailWatch WHERE TenantSectionDetailId = :tenantSectionDetailId ", nativeQuery = true)
    void deleteByTenantSectionDetailId(@Param("tenantSectionDetailId") Long tenantSectionDetailId);
}
