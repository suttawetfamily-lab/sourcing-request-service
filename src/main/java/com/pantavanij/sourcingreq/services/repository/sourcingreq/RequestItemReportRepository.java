package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItemReport;
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
public interface RequestItemReportRepository extends JpaRepository<RequestItemReport, Integer>, JpaSpecificationExecutor<RequestItemReport> {

    @Query(value = "SELECT * FROM RequestItemReport WHERE RecId = :requestItemReportId AND TenantId = :tenantId", nativeQuery = true)
    Optional<RequestItemReport> findRequestItemReportByRecIdAndTenant(@Param("requestItemReportId") Integer requestItemReportId,
                                                                      @Param("tenantId") Integer tenantId);

    @Query(value = "SELECT * FROM RequestItemReport WHERE TenantId = :tenantId", nativeQuery = true)
    List<RequestItemReport> findRequestItemReportByTenant(@Param("tenantId") Integer tenantId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM RequestItemReport WHERE RecId = :requestItemReportId", nativeQuery = true)
    void deleteRequestItemReportByRecId(@Param("requestItemReportId") Integer requestItemReportId);

    Optional<RequestItemReport> findFirstByTenantRecIdOrderBySequenceDesc(Integer tenantId);

    @Transactional
    @Modifying
    @Query(value =
            "UPDATE RequestItemReport " +
                    "SET Sequence = Sequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
                    "WHERE TenantId = :tenantId AND Sequence != :currentSequence " +
                    "AND Sequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
                    "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)",
            nativeQuery = true)
    void reOrderOtherRequestItemReportSequence(@Param("tenantId") Integer tenantId,
                                               @Param("currentSequence") Integer currentSequence,
                                               @Param("newSequence") Integer newSequence);
    List<RequestItemReport> findByMenuPrivilege_RecIdIn(List<Integer> menuPrivilegeIds);

    @Query(value =
            "SELECT * FROM RequestItemReport " +
                    "WHERE TenantId = :tenantId " +
                    "AND MenuPrivilegeId = :menuPrivilegeId " +
                    "AND PrivilegeCode = :privilegeCode " +
                    "AND Active = 1",
            nativeQuery = true)
    List<RequestItemReport> findActiveRequestItemReportByTenantAndMenuPrivilegeAndPrivilegeCode(
            @Param("tenantId") Integer tenantId,
            @Param("menuPrivilegeId") Integer menuPrivilegeId,
            @Param("privilegeCode") String privilegeCode
    );

    @Query(value =
            "SELECT * FROM RequestItemReport " +
                    "WHERE TenantId = :tenantId " +
                    "AND (:templateId IS NULL OR TemplateId = :templateId)",
            nativeQuery = true)
    List<RequestItemReport> findByTenantAndTemplate(@Param("tenantId") Integer tenantId,
                                                    @Param("templateId") Integer templateId);

    @Query(value =
            "SELECT * FROM RequestItemReport " +
                    "WHERE RecId = :recId " +
                    "AND TenantId = :tenantId " +
                    "AND TemplateId = :templateId",
            nativeQuery = true)
    Optional<RequestItemReport> findByRecIdAndTenantAndTemplate(@Param("recId") Integer recId,
                                                                @Param("tenantId") Integer tenantId,
                                                                @Param("templateId") Integer templateId);
}
