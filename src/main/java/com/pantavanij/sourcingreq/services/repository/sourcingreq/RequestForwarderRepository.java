package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestForwarder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestForwarderRepository extends JpaRepository<RequestForwarder, Integer> {

    @Query(value = "SELECT rf.* FROM RequestForwarder rf JOIN (SELECT ROW_NUMBER() OVER(PARTITION BY RequestId ORDER BY RecId DESC) AS 'LineNum', RecId, RequestId FROM RequestForwarder) t ON rf.RecId = t.RecId WHERE t.LineNum = 1 AND rf.RequestId = :requestId AND rf.ToApprover = :toApproverName", nativeQuery = true)
    Optional<RequestForwarder> getCurrentForwarderByRequestIdAndToApprovalName(@Param("requestId") Long requestId, @Param("toApproverName") String toApproverName);

    @Query(value = "SELECT TOP 1* FROM RequestForwarder rf WHERE rf.RequestId = :requestId ", nativeQuery = true)
    Optional<RequestForwarder> usedToForwardApprovalWorkflow(@Param("requestId") Long requestId);

    @Query(value = "SELECT TOP 1 rf.* FROM RequestForwarder rf WHERE rf.RequestId = :requestId ORDER BY rf.RecId DESC ", nativeQuery = true)
    Optional<RequestForwarder> findTop1ByRequestId(@Param("requestId") Long requestId);

    List<RequestForwarder> findByRequest(Request request);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    void deleteByRequest(Request request);
}
