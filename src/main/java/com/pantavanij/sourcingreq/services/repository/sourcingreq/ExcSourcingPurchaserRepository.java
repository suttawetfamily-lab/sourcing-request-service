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
public interface ExcSourcingPurchaserRepository extends JpaRepository<ExcSourcingPurchaser, Long> {

    List<ExcSourcingPurchaser> findExcSourcingPurchasersByExcSourcing(ExcSourcing excSourcing);

    List<ExcSourcingPurchaser> findExcSourcingPurchaserByApprover (Approver approver);

    List<ExcSourcingPurchaser> findExcSourcingPurchasersByExcSourcingAndApprovalStatus (ExcSourcing excSourcing, DeptApprovalStatus approvalStatus);

    Optional<ExcSourcingPurchaser> findExcSourcingPurchasersByExcSourcingAndApprover(ExcSourcing excSourcing, Approver approver);

    @Query(value = "SELECT TOP 1 * FROM ExcSourcingPurchaser WHERE ExcSourcingId = :excSourcingId AND ApprovalStatusId = 2 ORDER BY Sequence ", nativeQuery = true)
    Optional<ExcSourcingPurchaser> findCurrentAwaitingPurchaser(@Param("excSourcingId") Long excSourcingId);

    @Query(value = "SELECT TOP 1 * FROM ExcSourcingPurchaser WHERE ExcSourcingId = :excSourcingId AND ApprovalStatusId = 3 ORDER BY Sequence DESC ", nativeQuery = true)
    Optional<ExcSourcingPurchaser> findLatestApprovedPurchaser(@Param("excSourcingId") Long excSourcingId);

    @Query(value = "SELECT TOP 1 * FROM ExcSourcingPurchaser WHERE ExcSourcingId = :excSourcingId AND ApprovalStatusId = 5 ORDER BY Sequence ", nativeQuery = true)
    Optional<ExcSourcingPurchaser> find1StCancelledPurchaser(@Param("excSourcingId") Long excSourcingId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "DELETE FROM ExcSourcingPurchaser WHERE ExcSourcingId = :excSourcingId ", nativeQuery = true)
    void deleteExcSourcingPurchaserByExcSourcingId(@Param("excSourcingId") Long excSourcingId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    void deleteByExcSourcing(ExcSourcing excSourcing);


    @Query("SELECT ep FROM ExcSourcingPurchaser ep " +
            "WHERE ep.excSourcing.request.recId = :requestId")
    List<ExcSourcingPurchaser> findExcSourcingPurchasersByRequestId(@Param("requestId") Long requestId);

    // ตรวจว่ามี Purchaser Approver ที่ยัง pending อยู่หรือไม่
    @Query("SELECT CASE WHEN COUNT(ep) > 0 THEN true ELSE false END " +
            "FROM ExcSourcingPurchaser ep " +
            "WHERE ep.excSourcing = :excSourcing " +
            "AND ep.approvalStatus.recId = :statusId")
    boolean existsByExcSourcingAndApprovalStatusId(
            @Param("excSourcing") ExcSourcing excSourcing,
            @Param("statusId") Integer statusId
    );



}
