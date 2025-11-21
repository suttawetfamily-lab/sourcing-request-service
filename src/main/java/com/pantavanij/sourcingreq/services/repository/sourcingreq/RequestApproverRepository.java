package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Approver;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.DeptApprovalStatus;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestApprover;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestApproverRepository extends JpaRepository<RequestApprover, Long> {

    List<RequestApprover> findRequestApproversByRequest(Request request);

    List<RequestApprover> findRequestApproverByApprover (Approver approver);

    List<RequestApprover> findRequestApproversByRequestAndDeptApprovalStatus (Request request, DeptApprovalStatus deptApproverStatus);

    Optional<RequestApprover> findRequestApproversByRequestAndApprover(Request request, Approver approver);

    @Query(value = "SELECT TOP 1 * FROM RequestApprover WHERE RequestId = :requestId AND DeptApprovalStatusId = 2 ORDER BY Sequence ", nativeQuery = true)
    Optional<RequestApprover> findCurrentAwaitingDeptApprover(@Param("requestId") Long requestId);

    @Query(value = "SELECT TOP 1 * FROM RequestApprover WHERE RequestId = :requestId AND DeptApprovalStatusId = 3 ORDER BY Sequence DESC ", nativeQuery = true)
    Optional<RequestApprover> findLatestApprovedDeptApprover(@Param("requestId") Long requestId);

    @Query(value = "SELECT TOP 1 * FROM RequestApprover WHERE RequestId = :requestId AND DeptApprovalStatusId = 5 ORDER BY Sequence ", nativeQuery = true)
    Optional<RequestApprover> find1StCancelledDeptApprover(@Param("requestId") Long requestId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "DELETE FROM RequestApprover WHERE RequestId = :requestId ", nativeQuery = true)
    void deleteRequestApproverByRequestId(@Param("requestId") Long requestId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    void deleteByRequest(Request request);
}
