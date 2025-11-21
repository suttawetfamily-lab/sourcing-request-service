package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.*;

import java.util.*;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer>, JpaSpecificationExecutor<Project> {

    Project findProjectByRecId(Integer recId);

    @Query(value = "SELECT TOP 10 * FROM Project WHERE TenantId = :tenantId AND Active = 1", nativeQuery = true)
    List<Project> getProjectByTenantId(Integer tenantId);

    @Query(value = "SELECT TOP (:limit) * FROM Project WHERE TenantId = :tenantId AND Active = 1 AND (LOWER(Code) LIKE LOWER(CONCAT('%',:searchTerm, '%')) OR LOWER(Name) LIKE LOWER(CONCAT('%',:searchTerm, '%'))) ORDER BY sequence", nativeQuery = true)
    List<Project> getProjectByTenantIdAndSearchTerm(@Param("tenantId") Integer tenantId,
                                                    @Param("searchTerm") String searchTerm,
                                                    @Param("limit") Integer limit);

    Project findByCode(String code);

    @Query(value = "SELECT TOP 1 * FROM Project WHERE Code = :projectCode AND TenantId = :tenantId AND Active = 1", nativeQuery = true)
    Project getProjectByProjectCode(@Param("projectCode") String projectCode, @Param("tenantId") Integer tenantId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Project WHERE RecId = :recId AND TenantId = :tenantId", nativeQuery = true)
    void deleteByProjectRecIdAndTenantId(@Param("recId") Integer recId, @Param("tenantId") Integer tenantId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Project SET Sequence = Sequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId AND Sequence != :currentSequence " +
            "AND Sequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void reOrderOtherProjectSequences(@Param("tenantId") Integer tenantId, @Param("currentSequence") Integer currentSequence, @Param("newSequence") Integer newSequence);

    List<Project> findByTenant(Tenant tenant);

    Optional<Project> findFirstByTenantRecIdOrderBySequenceDesc(Integer tenantRecId);
}
