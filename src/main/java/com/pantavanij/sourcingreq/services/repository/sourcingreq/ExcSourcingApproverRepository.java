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
public interface ExcSourcingApproverRepository extends JpaRepository<ExcSourcingApprover, Long> {

    List<ExcSourcingApprover> findExcSourcingApproversByExcSourcing(ExcSourcing excSourcing);

    List<ExcSourcingApprover> findExcSourcingApproverByApprover (Approver approver);

    List<ExcSourcingApprover> findExcSourcingApproversByExcSourcingAndDeptApprovalStatus (ExcSourcing excSourcing, DeptApprovalStatus deptApproverStatus);

    Optional<ExcSourcingApprover> findExcSourcingApproversByExcSourcingAndApprover(ExcSourcing excSourcing, Approver approver);

    @Query(value = "SELECT TOP 1 * FROM ExcSourcingApprover WHERE ExcSourcingId = :excSourcingId AND DeptApprovalStatusId = 2 ORDER BY Sequence ", nativeQuery = true)
    Optional<ExcSourcingApprover> findCurrentAwaitingDeptApprover(@Param("excSourcingId") Long excSourcingId);

    @Query(value = "SELECT TOP 1 * FROM ExcSourcingApprover WHERE ExcSourcingId = :excSourcingId AND DeptApprovalStatusId = 3 ORDER BY Sequence DESC ", nativeQuery = true)
    Optional<ExcSourcingApprover> findLatestApprovedDeptApprover(@Param("excSourcingId") Long excSourcingId);

    @Query(value = "SELECT TOP 1 * FROM ExcSourcingApprover WHERE ExcSourcingId = :excSourcingId AND DeptApprovalStatusId = 5 ORDER BY Sequence ", nativeQuery = true)
    Optional<ExcSourcingApprover> find1StCancelledDeptApprover(@Param("excSourcingId") Long excSourcingId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "DELETE FROM ExcSourcingApprover WHERE ExcSourcingId = :excSourcingId ", nativeQuery = true)
    void deleteExcSourcingApproverByExcSourcingId(@Param("excSourcingId") Long excSourcingId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    void deleteByExcSourcing(ExcSourcing excSourcing);


    @Query("SELECT ea FROM ExcSourcingApprover ea " +
            "WHERE ea.excSourcing.request.recId = :requestId")
    List<ExcSourcingApprover> findExcSourcingApproversByRequestId(@Param("requestId") Long requestId);


    @Query("SELECT CASE WHEN COUNT(ea) > 0 THEN true ELSE false END " +
            "FROM ExcSourcingApprover ea " +
            "WHERE ea.excSourcing = :excSourcing " +
            "AND ea.deptApprovalStatus.recId = :statusId")
    boolean existsByExcSourcingAndDeptApprovalStatusId(
            @Param("excSourcing") ExcSourcing excSourcing,
            @Param("statusId") Integer statusId
    );





}
