package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Department;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer>, JpaSpecificationExecutor<Department> {

    Department findDepartmentByRecId(Integer recId);

    @Query(value = "SELECT TOP 10 d.* FROM Department d " +
            " JOIN TenantDepartmentOrganization tdo ON d.RecId = tdo.DepartmentId " +
            " WHERE d.TenantId = :tenantId AND d.Active = 1 AND LOWER(d.Name) LIKE LOWER(CONCAT('%',:searchTerm, '%')) AND tdo.OrganizationId = :organizationId ORDER BY Sequence", nativeQuery = true)
    List<Department> getDepartmentByTenantIdAndSearchTerm(@Param("tenantId") Integer tenantId,
                                                          @Param("searchTerm") String searchTerm,
                                                          @Param("organizationId") Integer organizationId);



    @Query(value = "SELECT * FROM Department WHERE Code = :departmentCode AND TenantId = :tenantId ", nativeQuery = true)
    Department getDepartmentByDepartmentCode(@Param("departmentCode") String departmentCode,
                                             @Param("tenantId") Integer tenantId);

    @Query(value = "SELECT * FROM Department WHERE RecId = :departmentId AND TenantId = :tenantId ", nativeQuery = true)
    Optional<Department> findDepartmentByRecIdAndTenantId(@Param("departmentId") Integer departmentId, @Param("tenantId") Integer tenantId);

    Optional<Department> findFirstByTenantRecIdOrderBySequenceDesc(Integer tenantId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Department SET Sequence = Sequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId AND Sequence != :currentSequence " +
            "AND Sequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void adjustOtherDepartmentSequences(
            @Param("tenantId") Integer tenantId,
            @Param("currentSequence") Integer currentSequence,
            @Param("newSequence") Integer newSequence);

    Optional<Department> findByCodeAndTenant_RecId(String departmentCode, Integer tenantId);

}
