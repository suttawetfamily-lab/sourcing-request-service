package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Requester;
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
public interface RequesterRepository extends JpaRepository<Requester, Integer>, JpaSpecificationExecutor<Requester> {

    @Query(value = "SELECT TOP 1 * FROM Requester WHERE UserId = :userId ", nativeQuery = true)
    Optional<Requester> findByUserId(@Param("userId") Integer userId);

    @Query(value = "SELECT TOP 1 * FROM Requester WHERE TenantId = :tenantId AND LoginId = :loginId ", nativeQuery = true)
    Optional<Requester> findByLoginId(@Param("tenantId") Integer tenantId, @Param("loginId") String loginId);

    @Query(value = "SELECT * FROM Requester WHERE RequesterName = :requesterName ", nativeQuery = true)
    List<Requester> findByRequesterName(@Param("requesterName") String requesterName);

    @Query(value = "SELECT * FROM Requester WHERE RequesterName = :requesterName ", nativeQuery = true)
    Optional<Requester> findByRequesterApproveName(@Param("requesterName") String requesterName);

    @Query(value = "SELECT * FROM Requester WHERE RecId = :requesterId AND TenantId = :tenantId ", nativeQuery = true)
    Optional<Requester> findRequesterByRecIdAndTenant(@Param("requesterId") Integer requesterId, @Param("tenantId") Integer tenantId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Requester WHERE RecId = :requesterId ", nativeQuery = true)
    void deleteRequesterByRecId(@Param("requesterId") Integer requesterId);

    Optional<Requester> findFirstByTenantRecIdOrderBySequenceDesc(Integer tenantId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Requester SET Sequence = Sequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId AND Sequence != :currentSequence " +
            "AND Sequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void reOrderOtherRequesterSequence(@Param("tenantId") Integer tenantId,
                                        @Param("currentSequence") Integer currentSequence,
                                        @Param("newSequence") Integer newSequence);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value =
        "WITH CTE AS ( " +
        "   SELECT " +
        "       * " +
        "       ,ROW_NUMBER() OVER (ORDER BY sequence) AS new_sequence " +
        "   FROM Requester " +
        "   WHERE TenantId = :tenantId) " +
        "UPDATE CTE " +
        "SET sequence = new_sequence " +
        "WHERE TenantId = :tenantId ",nativeQuery = true)
    void reOrderSequenceByTenantRecId(@Param("tenantId") Integer tenantId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value =
        "UPDATE r SET r.[Active] = 0 " +
                " FROM Requester r " +
                " WHERE r.RecId IN ( " +
                "    SELECT p.RecId " +
                "    FROM Requester p " +
                "    LEFT JOIN RequestRequester rp ON p.RecId = rp.RequesterId " +
                "    WHERE p.Active <> 0 " +
                "    GROUP BY p.TenantId, p.RecId " +
                "    HAVING COUNT(DISTINCT rp.RequesterId) = 0 " +
                ")",nativeQuery = true)
    int inActiveUnusedRequesters();
}
