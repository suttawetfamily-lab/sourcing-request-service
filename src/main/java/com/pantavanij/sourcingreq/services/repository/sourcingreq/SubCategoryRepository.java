package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SubCategory;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.*;

import javax.validation.constraints.*;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Integer> {

    SubCategory findSubCategoryByRecId(Integer recId);

    Optional<SubCategory> findByRecId(Integer recId);

    List<SubCategory> findByTenantRecIdAndCategory_RecId(Integer tenantId, Integer categoryId);

    @Query(value = "SELECT * FROM SubCategory WHERE TenantId = :tenantId", nativeQuery = true)
    List<SubCategory> findByTenantId(@Param("tenantId") Integer tenantId);

    @Query(value = "SELECT TOP 10 * FROM SubCategory WHERE TenantId = :tenantId " +
            "AND (LOWER(Code) LIKE LOWER(CONCAT('%',:searchTerm, '%')) " +
            "OR LOWER(Name) LIKE LOWER(CONCAT('%',:searchTerm, '%')))", nativeQuery = true)
    List<SubCategory> findByTenantIdAndSearchTerm(@Param("tenantId") Integer tenantId,
                                                  @Param("searchTerm") String searchTerm);

    @Query(value = "SELECT TOP 10 * FROM SubCategory WHERE TenantId = :tenantId " +
            "AND (:categoryId IS NULL OR CategoryId = :categoryId) " +
            "AND (LOWER(Code) LIKE LOWER(CONCAT('%',:searchTerm, '%')) " +
            "OR LOWER(Name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))", nativeQuery = true)
    List<SubCategory> findByTenantIdAndCategoryIdAndCodeOrName(@Param("tenantId") Integer tenantId,
                                                               @Param("categoryId") Integer categoryId,
                                                               @Param("searchTerm") String searchTerm);

    Optional<SubCategory> findByTenantAndCode(Tenant tenant, String code);

    Optional<SubCategory> findFirstByTenantRecIdAndCategoryRecIdOrderBySequenceDesc(Integer tenantRecId, Integer categoryRecId);

    void deleteAllByTenantRecIdAndCategoryRecId(Integer tenantRecId, Integer categoryRecId);

    Optional<SubCategory> findByRecIdAndTenant(Integer recId, Tenant tenant);

    @Transactional
    @Modifying
    @Query(value = "UPDATE SubCategory SET Sequence = Sequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId AND CategoryId = :categoryId " +
            "AND Sequence != :currentSequence " +
            "AND Sequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void reOrderOtherSubcategorySequence(
            @Param("tenantId") Integer tenantId,
            @Param("categoryId") Integer categoryId,
            @Param("currentSequence") Integer currentSequence,
            @Param("newSequence") Integer newSequence
    );


    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value =
            "WITH CTE AS ( " +
            "   SELECT " +
            "       * " +
            "       ,ROW_NUMBER() OVER (ORDER BY sequence) AS new_sequence " +
            "   FROM SubCategory " +
            "   WHERE TenantId = :tenantId " +
            "   AND CategoryId = :categoryId) " +
            "UPDATE CTE " +
            "SET sequence = new_sequence " +
            "WHERE TenantId = :tenantId " +
            "AND CategoryId = :categoryId",nativeQuery = true)
    void reOrderSequenceByTenantRecIdAndCategoryRecId(@Param("tenantId") Integer tenantId, @Param("categoryId") Integer categoryId);
}
