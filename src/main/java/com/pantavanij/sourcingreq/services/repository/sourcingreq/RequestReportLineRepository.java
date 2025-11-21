package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestReportLineRepository extends JpaRepository<RequestReportLine, Long> {

    @Query(value = "SELECT TOP 1 * FROM RequestReportLine WHERE RequestId = :requestId AND ReportLineId = :reportLineId ", nativeQuery = true)
    Optional<RequestReportLine> findRequestReportLineByRequestIdAndReportLineId(@Param("requestId") Long requestId, @Param("reportLineId") Long reportLineId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "DELETE FROM RequestReportLine WHERE RequestId = :requestId ", nativeQuery = true)
    void deleteRequestReportLineByRequestId(@Param("requestId") Long requestId);

    @Query(value = "SELECT * FROM RequestReportLine WHERE RequestId = :requestId ", nativeQuery = true)
    List<RequestReportLine> getByRequest(@Param("requestId") Long requestId);

    @Query(value = "SELECT * FROM RequestReportLine WHERE RequestId = :requestId AND ReportLineId = :reportLineId ", nativeQuery = true)
    Optional<RequestReportLine> findByRequestIdAndReportLineId(@Param("requestId") Long requestId, @Param("reportLineId") Long reportLineId);

    @Query(value = "SELECT * FROM RequestReportLine WHERE ReportLineId IN (:reportLineId) ", nativeQuery = true)
    List<RequestReportLine> findRequestReportLineByReportLineId(@Param("reportLineId") List<Long> reportLineId);
}
