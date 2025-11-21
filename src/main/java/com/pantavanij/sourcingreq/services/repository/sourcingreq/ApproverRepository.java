package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Approver;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.*;

import java.util.Optional;

@Repository
public interface ApproverRepository extends JpaRepository<Approver, Integer>, JpaSpecificationExecutor<Approver> {

    @Query(value = "SELECT TOP 1 * FROM Approver WHERE TenantId = :tenantId AND LoginId = :loginId ", nativeQuery = true)
    Optional<Approver> findByLoginId(@Param("tenantId") Integer tenantId, @Param("loginId") String loginId);
    Optional<Approver> findApproverByTenantAndLoginId(Tenant tenant, String loginId);

    Optional<Approver> findApproverByTenantAndUserId(Tenant tenant, Integer userId);

    @Query(value = "SELECT * FROM Approver WHERE RecId = :approverId AND TenantId = :tenantId ", nativeQuery = true)
    Optional<Approver> findApproverByRecIdAndTenant(@Param("approverId") Integer approverId, @Param("tenantId") Integer tenantId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Approver WHERE RecId = :approverId ", nativeQuery = true)
    void deleteApproverByRecId(@Param("approverId") Integer approverId);

    Optional<Approver> findFirstByTenantRecIdOrderBySequenceDesc(Integer tenantId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Approver SET Sequence = Sequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId AND Sequence != :currentSequence " +
            "AND Sequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void reOrderOtherApproverSequence(@Param("tenantId") Integer tenantId,
                                      @Param("currentSequence") Integer currentSequence,
                                      @Param("newSequence") Integer newSequence);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value =
            "UPDATE a SET a.[Active] = 0 " +
                    " FROM Approver a" +
                    " WHERE a.RecId IN ( " +
                    "    SELECT p.RecId " +
                    "    FROM Approver p " +
                    "    LEFT JOIN RequestApprover rp ON p.RecId = rp.ApproverId " +
                    "    WHERE p.Active <> 0 " +
                    "    GROUP BY p.TenantId, p.RecId " +
                    "    HAVING COUNT(DISTINCT rp.ApproverId) = 0 " +
                    ")",nativeQuery = true)
    int inActiveUnusedApprovers();
}
