package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Location;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {

    @Query(value = "SELECT l.* FROM Location l " +
            " JOIN TenantLocationOrganization tlo ON l.RecId = tlo.LocationId WHERE l.TenantId = :tenantId AND (tlo.OrganizationId = 0 OR tlo.OrganizationId = :organizationId) ORDER BY sequence ", nativeQuery = true)
    List<Location> getLocationByTenantId(@Param("tenantId") Integer tenantId, @Param("organizationId") Integer organizationId);

    @Query(value = "SELECT TOP 1 ISNULL(tlo.OrganizationCode, '') AS OrganizationCode FROM Location l " +
            " JOIN TenantLocationOrganization tlo ON l.RecId = tlo.LocationId " +
            " WHERE l.TenantId = :tenantId AND tlo.LocationId = :locationId ", nativeQuery = true)
    String getOrganizationCodeByLocation(@Param("tenantId") Integer tenantId, @Param("locationId") Integer locationId);


    Optional<Location> findByTenantAndName(Tenant tenant, String name);
}
