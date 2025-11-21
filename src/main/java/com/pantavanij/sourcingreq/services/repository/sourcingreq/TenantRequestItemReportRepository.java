package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantRequestItemReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TenantRequestItemReportRepository extends JpaRepository<TenantRequestItemReport, Integer>, JpaSpecificationExecutor<TenantRequestItemReport> {

    List<TenantRequestItemReport> findByTenantRecIdAndRequestItemReportRecId(
            Integer tenantId,
            Integer requestItemReportRecId
    );

    // ✅ ใช้ reorder sequence สำหรับกรณีไม่มี template
    @Transactional
    @Modifying
    @Query(value =
            "UPDATE TenantRequestItemReport " +
                    "SET Sequence = Sequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
                    "WHERE TenantId = :tenantId " +
                    "AND Sequence != :currentSequence " +
                    "AND Sequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
                    "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)",
            nativeQuery = true)
    void reOrderOtherTenantRequestItemReportSequence(
            @Param("tenantId") Integer tenantId,
            @Param("currentSequence") Integer currentSequence,
            @Param("newSequence") Integer newSequence
    );

    // ✅ ใช้ reorder sequence สำหรับกรณีมี template
    @Transactional
    @Modifying
    @Query(value =
            "UPDATE TenantRequestItemReport " +
                    "SET Sequence = Sequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
                    "WHERE TenantId = :tenantId " +
                    "AND (:templateId IS NULL OR TemplateId = :templateId) " +
                    "AND Sequence != :currentSequence " +
                    "AND Sequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
                    "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)",
            nativeQuery = true)
    void reOrderOtherTenantRequestItemReportSequenceWithTemplate(
            @Param("tenantId") Integer tenantId,
            @Param("templateId") Integer templateId,
            @Param("currentSequence") Integer currentSequence,
            @Param("newSequence") Integer newSequence
    );

    // ✅ ใช้สำหรับลบ tenantRequestItemReport record เฉพาะ
    @Transactional
    @Modifying
    @Query(value =
            "DELETE FROM TenantRequestItemReport " +
                    "WHERE TenantId = :tenantId " +
                    "AND RequestItemReportId = :requestItemReportId " +
                    "AND TenantSectionDetailId = :tenantSectionDetailId",
            nativeQuery = true)
    void deleteTenantRequestItemReportByIds(
            @Param("tenantId") Integer tenantId,
            @Param("requestItemReportId") Integer requestItemReportId,
            @Param("tenantSectionDetailId") Integer tenantSectionDetailId
    );
}
