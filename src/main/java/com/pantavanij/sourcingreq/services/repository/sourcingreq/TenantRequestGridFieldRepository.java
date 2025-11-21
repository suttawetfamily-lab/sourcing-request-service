package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantRequestGridField;
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
public interface TenantRequestGridFieldRepository extends JpaRepository<TenantRequestGridField, Integer>, JpaSpecificationExecutor<TenantRequestGridField> {

    List<TenantRequestGridField> findByPrivilegeCodeAndTenant_CodeOrderBySequence(String privilegeCode, String tenantCode);

    List<TenantRequestGridField> findByPrivilegeCodeAndTenant_CodeAndSearchableOrderBySequence(
            String privilegeCode, String tenantCode, boolean searchable);
    List<TenantRequestGridField> findByTenant_CodeAndSearchableOrderBySequence(String tenantCode, boolean searchable);

    @Query(value = "SELECT * FROM TenantRequestGridField WHERE RequestGridFieldId = :requestGridFieldId AND PrivilegeCode = :privilegeCode AND TenantId = :tenantId ", nativeQuery = true)
    Optional<TenantRequestGridField> findTenantRequestGridFieldByRecIdAndPrivilegeCodeAndTenantId(@Param("requestGridFieldId") Integer requestGridFieldId, @Param("privilegeCode") String privilegeCode, @Param("tenantId") Integer tenantId);

    @Query(value = "SELECT * FROM TenantRequestGridField WHERE Code = :code AND PrivilegeCode = :privilegeCode AND TenantId = :tenantId ", nativeQuery = true)
    Optional<TenantRequestGridField> findTenantRequestGridFieldByCodeAndPrivilegeCodeAndTenantId(@Param("code") String code, @Param("privilegeCode") String privilegeCode, @Param("tenantId") Integer tenantId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE TenantRequestGridField SET Sequence = Sequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId AND PrivilegeCode = :privilegeCode AND Sequence != :currentSequence " +
            "AND Sequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void adjustOtherTenantRequestGridFieldSequences(@Param("privilegeCode") String privilegeCode, @Param("tenantId") Integer tenantId, @Param("currentSequence") Integer currentSequence, @Param("newSequence") Integer newSequence);

    Optional<TenantRequestGridField> findFirstByTenantRecIdOrderBySequenceDesc(Integer tenantId);

    List<TenantRequestGridField> findByPrivilegeCodeAndTenant_CodeAndTemplateRecIdOrderBySequence(
            String privilegeCode, String tenantCode, Integer templateId);

    @Query("SELECT trg FROM TenantRequestGridField trg " +
            "JOIN trg.tenant t " +
            "JOIN trg.template tt " +
            "JOIN TenantOrganizationTemplate tot ON tot.id.templateId = tt.recId " +
            "WHERE trg.privilegeCode = :privilegeCode " +
            "AND t.code = :tenantCode " +
            "AND trg.searchable = true " +
            "AND (:organizationId IS NULL OR tot.id.organizationId = :organizationId) " +
            "ORDER BY trg.sequence")
    List<TenantRequestGridField> findSearchableWithOrganization(
            @Param("privilegeCode") String privilegeCode,
            @Param("tenantCode") String tenantCode,
            @Param("organizationId") Integer organizationId);


}
