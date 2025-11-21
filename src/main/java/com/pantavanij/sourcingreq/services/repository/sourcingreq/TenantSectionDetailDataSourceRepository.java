package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.util.Optional;

@Repository
public interface TenantSectionDetailDataSourceRepository extends JpaRepository<TenantSectionDetailDataSource, Integer> {

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "DELETE FROM TenantSectionDetailDataSource WHERE TenantSectionDetailId = :tenantSectionDetailId ", nativeQuery = true)
    void deleteByTenantSectionDetailId(@Param("tenantSectionDetailId") Long tenantSectionDetailId);

    @Query(value = "SELECT TOP 1 * FROM TenantSectionDetailDataSource WHERE TenantSectionDetailId = :tenantSectionDetailId", nativeQuery = true)
    Optional<TenantSectionDetailDataSource> findTop1ByTenantSectionDetailId(@Param("tenantSectionDetailId") Long tenantSectionDetailId);

}
