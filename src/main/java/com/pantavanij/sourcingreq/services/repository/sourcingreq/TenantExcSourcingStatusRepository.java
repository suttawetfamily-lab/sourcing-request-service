package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantExcSourcingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantExcSourcingStatusRepository extends JpaRepository<TenantExcSourcingStatus, Integer> {

    List<TenantExcSourcingStatus> findByTenant_Code(String tenantCode);
    @Query(value = "SELECT TOP 10 * FROM TenantExcSourcingStatus WHERE TenantId = :tenantId " +
            "AND (LOWER(Name) LIKE LOWER(CONCAT('%',:searchTerm, '%')) " +
            "OR LOWER(Description) LIKE LOWER(CONCAT('%',:searchTerm, '%')))", nativeQuery = true)
    List<TenantExcSourcingStatus> getTenantExcSourcingStatusByTenantIdAndSearchTerm(@Param("tenantId") Integer tenantId,
                                                        @Param("searchTerm") String searchTerm);

    @Query(value = "SELECT * FROM TenantExcSourcingStatus WHERE TenantId = :tenantId AND Name IN :name", nativeQuery = true)
    List<TenantExcSourcingStatus> findByTenantIdAndNameIn(@Param("tenantId") Integer tenantId,
                                                      @Param("name") List<String> name);

    TenantExcSourcingStatus findByNameAndTenant (String name, Tenant tenant);

    List<TenantExcSourcingStatus> findByTenant_CodeAndNameNotIn(String tenantCode, List<String> excludedStatus);

    List<TenantExcSourcingStatus> findById_ExcSourcingStatusIdIn(List<Integer> ids);


}
