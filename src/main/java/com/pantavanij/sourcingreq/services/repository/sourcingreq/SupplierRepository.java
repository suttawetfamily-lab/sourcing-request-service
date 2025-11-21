package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Supplier;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
    Optional<Supplier> findSupplierByRecId(Integer recId);

    @Query(value = "SELECT Supplier.* FROM Supplier WHERE TenantId = :tenantId AND LOWER(ShortName) = LOWER(:shortName) ", nativeQuery = true)
    Supplier getSupplierByShortName(@Param("shortName") String shortName, @Param("tenantId") Integer tenantId);

    @Query(value = "SELECT TOP 1 * FROM Supplier WHERE TenantId = :tenantId ORDER BY RecId ", nativeQuery = true)
    Supplier getFirstSupplierByTenantId(@Param("tenantId") Integer tenantId);

    Optional<Supplier> getSupplierByTenantAndShortName(Tenant tenant, String shortName);

    List<Supplier> findAllByTenant(Tenant tenant);

    @Query(value = "SELECT DISTINCT TOP 5 s.*\n" +
            "FROM Request r\n" +
            "JOIN RequestItem ri ON r.RecId = ri.RequestId\n" +
            "JOIN ExistingPriceItem epi ON ri.RecId = epi.RequestItemId\n" +
            "JOIN ExistingPriceItemSupplier epis ON epi.RecId = epis.ExistingPriceItemId\n" +
            "JOIN Supplier s ON epis.SupplierId = s.RecId\n" +
            "\n" +
            "WHERE r.TenantId = :tenantId AND r.StatusId = 4 AND r.ApprovalStatusId = 4 --Completed\n" +
            "AND ri.SourcingStatusId IN (9) -- = 9 --Qualified\n" +
            "AND s.ActiveOnERP = 0\n" +
            "AND s.TaxId IS NOT NULL\n" +
            "AND ( :applyOrgFilter = 0 OR r.OrganizationId IN (:organizationIds) ) " +
            "ORDER BY RecId ", nativeQuery = true)
    List<Supplier> getERPSupplierByTenantAndSourcingStatus(@Param("tenantId") Integer tenantId,
                                                           @Param("applyOrgFilter") int applyOrgFilter,
                                                           @Param("organizationIds") List<Integer> organizationIds);

}
