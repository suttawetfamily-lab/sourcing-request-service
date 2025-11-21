package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantRequestReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface TenantRequestReportRepository extends JpaRepository<TenantRequestReport, Integer>, JpaSpecificationExecutor<TenantRequestReport> {
    Optional<TenantRequestReport> findByTenantRecIdAndRequestReportRecId(Integer tenantId, Integer requestReportRecId);
    Optional<TenantRequestReport> findFirstByTenantRecIdOrderBySequenceDesc(Integer tenantId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE TenantRequestReport SET Sequence = Sequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId AND Sequence != :currentSequence " +
            "AND Sequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void reOrderOtherTenantRequestReportSequence(@Param("tenantId") Integer tenantId,
                                            @Param("currentSequence") Integer currentSequence,
                                            @Param("newSequence") Integer newSequence);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM TenantRequestReport WHERE TenantId = :tenantId AND RequestReportId = :requestReportId AND TenantSectionDetailId = :tenantSectionDetailId", nativeQuery = true)
    void deleteTenantRequestReportByIds(
            @Param("tenantId") Integer tenantId,
            @Param("requestReportId") Integer requestReportId,
            @Param("tenantSectionDetailId") Integer tenantSectionDetailId
    );
}
