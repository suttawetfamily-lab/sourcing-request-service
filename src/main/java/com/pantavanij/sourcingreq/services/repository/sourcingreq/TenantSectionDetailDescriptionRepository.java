package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;

import javax.validation.constraints.*;
import java.util.*;

@Repository
public interface TenantSectionDetailDescriptionRepository extends JpaRepository<TenantSectionDetailDescription, Integer>, JpaSpecificationExecutor<TenantSectionDetailDescription> {
    Optional<TenantSectionDetailDescription> findByTenantSectionDetail_Id(Long tenantSectionDetailId);

    @Modifying
    @Query(value = "DELETE FROM TenantSectionDetailDescription WHERE TenantSectionDetailId = :tenantSectionDetailId ", nativeQuery = true)
    void deleteByTenantSectionDetailId(@Param("tenantSectionDetailId") Long tenantSectionDetailId);
}
