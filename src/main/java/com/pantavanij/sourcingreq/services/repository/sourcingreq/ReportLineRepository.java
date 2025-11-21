package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportLineRepository extends JpaRepository<ReportLine, Long>, JpaSpecificationExecutor<ReportLine> {

    @Query(value = "SELECT TOP 1 * FROM ReportLine WHERE UserId = :userId ", nativeQuery = true)
    Optional<ReportLine> findByUserId(@Param("userId") Integer userId);

    @Query(value = "SELECT TOP 1 * FROM ReportLine WHERE TenantId = :tenantId AND LoginId = :loginId ", nativeQuery = true)
    Optional<ReportLine> findByLoginId(@Param("tenantId") Integer tenantId, @Param("loginId") String loginId);

    @Query(value = "SELECT * FROM ReportLine WHERE ReportLineName = :reportLineName ", nativeQuery = true)
    List<ReportLine> findByApproverName(@Param("reportLineName") String reportLineName);

    @Query(value = "SELECT * FROM ReportLine WHERE ReportLineName = :reportLineName ", nativeQuery = true)
    Optional<ReportLine> findByReportLineApproveName(@Param("reportLineName") String reportLineName);

    @Query(value = "SELECT * FROM ReportLine WHERE RecId = :reportLineId AND TenantId = :tenantId ", nativeQuery = true)
    Optional<ReportLine> findReportLineByRecIdAndTenant(@Param("reportLineId") Integer reportLineId, @Param("tenantId") Integer tenantId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM ReportLine WHERE RecId = :reportLineId ", nativeQuery = true)
    void deleteReportLineByRecId(@Param("reportLineId") Integer reportLineId);

    Optional<ReportLine> findFirstByTenantRecIdOrderBySequenceDesc(Integer tenantId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE ReportLine SET Sequence = Sequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId AND Sequence != :currentSequence " +
            "AND Sequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void reOrderOtherReportLineSequence(@Param("tenantId") Integer tenantId,
                                        @Param("currentSequence") Integer currentSequence,
                                        @Param("newSequence") Integer newSequence);

    void deleteByRecIdAndTenant(Long reportLineId, Tenant tenant);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value =
        "WITH CTE AS ( " +
        "   SELECT " +
        "       * " +
        "       ,ROW_NUMBER() OVER (ORDER BY sequence) AS new_sequence " +
        "   FROM ReportLine " +
        "   WHERE TenantId = :tenantId) " +
        "UPDATE CTE " +
        "SET sequence = new_sequence " +
        "WHERE TenantId = :tenantId ",nativeQuery = true)
    void reOrderSequenceByTenantRecId(@Param("tenantId") Integer tenantId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value =
            "UPDATE r SET r.[Active] = 0 " +
                    " FROM ReportLine r " +
                    " WHERE r.RecId IN ( " +
                    "    SELECT p.RecId " +
                    "    FROM ReportLine p " +
                    "    LEFT JOIN RequestReportLine rp ON p.RecId = rp.ReportLineId " +
                    "    WHERE p.Active <> 0 " +
                    "    GROUP BY p.TenantId, p.RecId " +
                    "    HAVING COUNT(DISTINCT rp.ReportLineId) = 0 " +
                    ")",nativeQuery = true)
    int inActiveUnusedReportLines();
}
