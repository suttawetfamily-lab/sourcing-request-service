package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Purpose;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurposeRepository extends JpaRepository<Purpose, Integer> {

    Purpose findPurposeByRecId(Integer recId);

    @Query(value = "SELECT * FROM Purpose WHERE TenantId = :tenantId", nativeQuery = true)
    List<Purpose> getPurposeByTenantId(@Param("tenantId") Integer tenantId);

    @Query(value = "SELECT TOP 10 * FROM Purpose WHERE TenantId = :tenantId AND (LOWER(Code) LIKE LOWER(CONCAT('%',:searchTerm, '%')) OR LOWER(Name) LIKE LOWER(CONCAT('%',:searchTerm, '%')))", nativeQuery = true)
    List<Purpose> getPurposeByTenantIdAndSearchTerm(@Param("tenantId") Integer tenantId,
                                                    @Param("searchTerm") String searchTerm);
    
    Optional<Purpose> findByTenantAndCode(Tenant tenant, String code);
}
