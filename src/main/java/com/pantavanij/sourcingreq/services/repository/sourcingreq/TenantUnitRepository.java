package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.projection.TenantUnitOptionProjection;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.util.*;

@Repository
public interface TenantUnitRepository extends JpaRepository<TenantUnit, Integer>, JpaSpecificationExecutor<TenantUnit> {

    @Query(value = "SELECT TenantUnit.* FROM TenantUnit WHERE UnitId = :unitId AND TenantId = :tenantId", nativeQuery = true)
    TenantUnit findTenantUnitByUnitIdAndTenantId(@Param("unitId") Integer unitId,
                                                 @Param("tenantId") Integer tenantId);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "INSERT INTO TenantUnit(TenantId, UnitId, CreatedBy, CreatedDate) VALUES(:tenantId, :unitId, :createdBy, :createdDate)", nativeQuery = true)
    void saveTenantUnit(@Param("tenantId") Integer tenantId,
                        @Param("unitId") Integer unitId,
                        @Param("createdBy") String createdBy,
                        @Param("createdDate") Timestamp createdDate);

    Optional<TenantUnit> findByTenantRecIdAndUnitRecId(Integer tenantId, Integer unitId);
    Optional<TenantUnit> findFirstByTenantRecIdOrderBySequenceDesc(Integer tenantId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE TenantUnit SET Sequence = Sequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId AND Sequence != :currentSequence " +
            "AND Sequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void reOrderOtherTenantUnitSequence(@Param("tenantId") Integer tenantId,
                                            @Param("currentSequence") Integer currentSequence,
                                            @Param("newSequence") Integer newSequence);

    @Query(value = "SELECT c.RecId AS value, c.Code AS code, c.Name AS name, " +
            "tc.Sequence AS sequence, tc.Active AS active, tc.[Default] AS isDefault " +
            "FROM Unit c " +
            "INNER JOIN TenantUnit tc ON c.RecId = tc.UnitId " +
            "WHERE tc.Active = 1 AND tc.TenantId = :tenantId " +
            "AND (LOWER(c.Code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(c.Name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "ORDER BY tc.Sequence", nativeQuery = true)
    List<TenantUnitOptionProjection> findByTenantIdAndCodeOrDescription(
            @Param("tenantId") Integer tenantId,
            @Param("searchTerm") String searchTerm);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    void deleteByTenantRecIdAndUnitRecId(Integer tenantId, Integer unitId);

    @Query(value = "SELECT c.RecId AS value, c.Code AS code, c.Name AS name, " +
            "tc.Sequence AS sequence, tc.Active AS active, tc.[Default] AS isDefault " +
            "FROM Unit c " +
            "INNER JOIN TenantUnit tc ON c.RecId = tc.UnitId " +
            "WHERE tc.Active = 1 AND tc.TenantId = :tenantId " +
            "AND tc.OrganizationId = :organizationId " +
            "AND (LOWER(c.Code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(c.Name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "ORDER BY tc.Sequence", nativeQuery = true)
    List<TenantUnitOptionProjection> findByTenantIdAndOrganizationIdAndCodeOrDescription(
            @Param("tenantId") Integer tenantId,
            @Param("organizationId") Integer organizationId,
            @Param("searchTerm") String searchTerm);

}
