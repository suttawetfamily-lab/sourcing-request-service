package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Category;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.*;

import javax.validation.constraints.*;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>, JpaSpecificationExecutor<Category> {

    Category findCategoryByRecId(Integer recId);

    Optional<Category> findFirstByRecId(Integer recId);

    Optional<Category> findByRecIdAndTenant(Integer recId, Tenant tenant);

    @Query(value = "SELECT * FROM Category WHERE TenantId = :tenantId", nativeQuery = true)
    List<Category> findByTenantId(@Param("tenantId") Integer tenantId);

    @Query(value = "SELECT TOP 10 c.* FROM Category c " +
            "INNER JOIN TenantCategoryOrganization tco ON c.TenantId = tco.TenantId AND c.RecId = tco.CategoryId " +
            "WHERE c.TenantId = :tenantId " +
            "AND (LOWER(c.Code) LIKE LOWER(CONCAT('%',:searchTerm, '%')) " +
            "OR LOWER(c.Name) LIKE LOWER(CONCAT('%',:searchTerm, '%'))) " +
            "AND tco.OrganizationId = :organizationId ", nativeQuery = true)
    List<Category> findByTenantIdAndCodeOrName(@Param("tenantId") Integer tenantId,
                                               @Param("searchTerm") String searchTerm,
                                               @Param("organizationId") Integer organizationId);
    
    Optional<Category> findByTenantAndCode(Tenant tenant, String code);

    Optional<Category> findFirstByTenantRecIdOrderBySequenceDesc(Integer recId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "DELETE FROM Category WHERE RecId = :recId ", nativeQuery = true)
    int deleteByRecId(@Param("recId") Integer recId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "UPDATE Category SET Sequence = Sequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId AND Sequence != :currentSequence " +
            "AND Sequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void reOrderOtherCategorySequence(@Param("tenantId") Integer tenantId,
                                      @Param("currentSequence") Integer currentSequence,
                                      @Param("newSequence") Integer newSequence);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value =
            "WITH CTE AS ( " +
            "   SELECT " +
            "       * " +
            "       ,ROW_NUMBER() OVER (ORDER BY sequence) AS new_sequence " +
            "   FROM Category " +
            "   WHERE TenantId = :tenantId) " +
            "UPDATE CTE " +
            "SET sequence = new_sequence " +
            "WHERE TenantId = :tenantId ",nativeQuery = true)
    void reOrderSequenceByTenantRecId(@Param("tenantId") Integer tenantId);
}
