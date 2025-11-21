package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Reviewer;
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
public interface ReviewerRepository extends JpaRepository<Reviewer, Integer>, JpaSpecificationExecutor<Reviewer> {

    @Query(value = "SELECT TOP 1 * FROM Reviewer WHERE UserId = :userId ", nativeQuery = true)
    Optional<Reviewer> findByUserId(@Param("userId") Integer userId);

    @Query(value = "SELECT TOP 1 * FROM Reviewer WHERE TenantId = :tenantId AND LoginId = :loginId ", nativeQuery = true)
    Optional<Reviewer> findByLoginId(@Param("tenantId") Integer tenantId, @Param("loginId") String loginId);

    @Query(value = "SELECT * FROM Reviewer WHERE ReviewerName = :reviewerName ", nativeQuery = true)
    List<Reviewer> findByReviewerName(@Param("reviewerName") String reviewerName);

    @Query(value = "SELECT * FROM Reviewer WHERE ReviewerName = :reviewerName ", nativeQuery = true)
    Optional<Reviewer> findByReviewerApproveName(@Param("reviewerName") String reviewerName);

    @Query(value = "SELECT * FROM Reviewer WHERE RecId = :reviewerId AND TenantId = :tenantId ", nativeQuery = true)
    Optional<Reviewer> findReviewerByRecIdAndTenant(@Param("reviewerId") Integer reviewerId, @Param("tenantId") Integer tenantId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Reviewer WHERE RecId = :reviewerId ", nativeQuery = true)
    void deleteReviewerByRecId(@Param("reviewerId") Integer reviewerId);

    Optional<Reviewer> findFirstByTenantRecIdOrderBySequenceDesc(Integer tenantId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Reviewer SET Sequence = Sequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId AND Sequence != :currentSequence " +
            "AND Sequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void reOrderOtherReviewerSequence(@Param("tenantId") Integer tenantId,
                                        @Param("currentSequence") Integer currentSequence,
                                        @Param("newSequence") Integer newSequence);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value =
        "WITH CTE AS ( " +
        "   SELECT " +
        "       * " +
        "       ,ROW_NUMBER() OVER (ORDER BY sequence) AS new_sequence " +
        "   FROM Reviewer " +
        "   WHERE TenantId = :tenantId) " +
        "UPDATE CTE " +
        "SET sequence = new_sequence " +
        "WHERE TenantId = :tenantId ",nativeQuery = true)
    void reOrderSequenceByTenantRecId(@Param("tenantId") Integer tenantId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value =
            "UPDATE r SET r.[Active] = 0 " +
                    " FROM Reviewer r " +
                    " WHERE r.RecId IN ( " +
                    "    SELECT p.RecId " +
                    "    FROM Reviewer p " +
                    "    LEFT JOIN RequestReviewer rp ON p.RecId = rp.ReviewerId " +
                    "    WHERE p.Active <> 0 " +
                    "    GROUP BY p.TenantId, p.RecId " +
                    "    HAVING COUNT(DISTINCT rp.ReviewerId) = 0 " +
                    ")",nativeQuery = true)
    int inActiveUnusedReviewers();
}
