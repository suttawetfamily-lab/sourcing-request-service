package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItemReport;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestReportRepository extends JpaRepository<RequestReport, Integer>, JpaSpecificationExecutor<RequestReport> {

    Optional<RequestReport> findRequestReportByRecIdAndTenantRecId(Integer recId, Integer tenantId);

    @Query(value = "SELECT * FROM RequestReport WHERE RecId = :requestReportId AND TenantId = :tenantId ", nativeQuery = true)
    Optional<RequestReport> findRequestReportByRecIdAndTenant(@Param("requestReportId") Integer requestReportId, @Param("tenantId") Integer tenantId);

    @Query(value = "SELECT TOP 1 * FROM RequestReport " +
            "WHERE Active = 1 AND TenantId = :tenantId AND PrivilegeCode = :privilegeCode ", nativeQuery = true)
    Optional<RequestReport> findByTenantIdAndPrivilegeCode(@Param("tenantId") Integer tenantId, @Param("privilegeCode") String privilegeCode);

    Optional<RequestReport> findRequestReportByRecId(Integer recId);

    @Query(value = "SELECT RequestReport.* FROM RequestReport " +
            "INNER JOIN TenantRequestReport ON RequestReport.RecId = TenantRequestReport.RequestReportId " +
            "WHERE Active = 1 AND TenantRequestReport.TenantId = :tenantId", nativeQuery = true)
    List<RequestReport> findByTenantId(@Param("tenantId") Integer tenantId);

    @Query(value = "SELECT RequestReport.* FROM RequestReport " +
            "INNER JOIN TenantRequestReport ON RequestReport.RecId = TenantRequestReport.RequestReportId " +
            "WHERE Active = 1 AND TenantRequestReport.TenantId = :tenantId " +
            "AND (LOWER(RequestReport.Code) LIKE LOWER(CONCAT('%',:searchTerm, '%')) " +
            "OR LOWER(RequestReport.Name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) ORDER BY RequestReport.Code"
            , nativeQuery = true)
    List<RequestReport> findByTenantIdAndCodeOrDescription(@Param("tenantId") Integer tenantId,
                                                      @Param("searchTerm") String searchTerm);

    Optional<RequestReport> findByRecId(Integer requestReportId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM RequestReport WHERE RecId = :requestReportId ", nativeQuery = true)
    void deleteRequestReportByRecId(@Param("requestReportId") Integer requestReportId);

    Optional<RequestReport> findFirstByTenantRecIdOrderBySequenceDesc(Integer tenantId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE RequestReport SET Sequence = Sequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId AND Sequence != :currentSequence " +
            "AND Sequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void reOrderOtherRequestReportSequence(@Param("tenantId") Integer tenantId,
                                               @Param("currentSequence") Integer currentSequence,
                                               @Param("newSequence") Integer newSequence);
}
