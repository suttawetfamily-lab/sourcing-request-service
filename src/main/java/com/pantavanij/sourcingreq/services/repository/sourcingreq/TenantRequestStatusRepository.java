package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Objective;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantRequestStatusRepository extends JpaRepository<TenantRequestStatus, Integer> {

    List<TenantRequestStatus> findByTenant_Code(String tenantCode);
    @Query(value = "SELECT TOP 10 * FROM TenantRequestStatus WHERE TenantId = :tenantId " +
            "AND (LOWER(Name) LIKE LOWER(CONCAT('%',:searchTerm, '%')) " +
            "OR LOWER(Description) LIKE LOWER(CONCAT('%',:searchTerm, '%')))", nativeQuery = true)
    List<TenantRequestStatus> getTenantRequestStatusByTenantIdAndSearchTerm(@Param("tenantId") Integer tenantId,
                                                        @Param("searchTerm") String searchTerm);

    @Query(value = "SELECT * FROM TenantRequestStatus WHERE TenantId = :tenantId AND Name IN :name", nativeQuery = true)
    List<TenantRequestStatus> findByTenantIdAndNameIn(@Param("tenantId") Integer tenantId,
                                                      @Param("name") List<String> name);

    TenantRequestStatus findByNameAndTenant (String name, Tenant tenant);

    List<TenantRequestStatus> findByTenant_CodeAndNameNotIn(String tenantCode, List<String> excludedStatus);

}
