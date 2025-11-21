package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantUnitOrganization;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.TenantUnitOrganizationKey;
import com.pantavanij.sourcingreq.services.domain.projection.TenantUnitOptionProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantUnitOrganizationRepository extends JpaRepository<TenantUnitOrganization, TenantUnitOrganizationKey> {

    @Query(value =
            "SELECT " +
                    "  c.RecId AS value, " +
                    "  c.Code AS code, " +
                    "  c.Name AS name, " +
                    "  0 AS sequence, " +     // dummy ให้ตรง projection
                    "  CAST(1 AS bit) AS active, " + // dummy boolean
                    "  CAST(0 AS bit) AS isDefault " + // dummy boolean
                    "FROM Unit c " +
                    "INNER JOIN TenantUnit tu ON c.RecId = tu.UnitId " +
                    "INNER JOIN TenantUnitOrganization tuno ON tu.UnitId = tuno.UnitId AND tu.TenantId = tuno.TenantId " +
                    "WHERE tuno.TenantId = :tenantId " +
                    "  AND tu.Active = 1 " +
                    "  AND tuno.OrganizationId = :organizationId " +
                    "  AND (LOWER(c.Code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
                    "       OR LOWER(c.Name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
                    "ORDER BY c.Name",
            nativeQuery = true)
    List<TenantUnitOptionProjection> findByTenantIdAndOrganizationIdAndSearchTerm(
            @Param("tenantId") Integer tenantId,
            @Param("organizationId") Integer organizationId,
            @Param("searchTerm") String searchTerm);

}
