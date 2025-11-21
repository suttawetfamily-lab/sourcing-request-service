package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantRequestItemGridField;
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
public interface TenantRequestItemGridFieldRepository extends JpaRepository<TenantRequestItemGridField, Integer>, JpaSpecificationExecutor<TenantRequestItemGridField> {

    List<TenantRequestItemGridField> findByPrivilegeCodeAndTenant_CodeAndTemplate_RecIdAndVisibleOrderBySequence(
            String privilegeCode, String tenantCode, Integer templateId, boolean visible);

    List<TenantRequestItemGridField> findByTenant_CodeAndVisibleOrderBySequence(String tenantCode, boolean visible);

    @Query(value = "SELECT * FROM TenantRequestItemGridField " +
            "WHERE RequestItemGridFieldId = :requestItemGridFieldId " +
            "AND PrivilegeCode = :privilegeCode " +
            "AND TenantId = :tenantId " +
            "AND TemplateId = :templateId",
            nativeQuery = true)
    Optional<TenantRequestItemGridField> findByRecIdAndPrivilegeCodeAndTenantIdAndTemplateId(
            @Param("requestItemGridFieldId") Integer requestItemGridFieldId,
            @Param("privilegeCode") String privilegeCode,
            @Param("tenantId") Integer tenantId,
            @Param("templateId") Integer templateId);

    Optional<TenantRequestItemGridField> findFirstByTenantRecIdAndId_TemplateIdOrderBySequenceDesc(Integer tenantId, Integer templateId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE TenantRequestItemGridField " +
            "SET Sequence = Sequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId " +
            "AND PrivilegeCode = :privilegeCode " +
            "AND TemplateId = :templateId " +
            "AND Sequence != :currentSequence " +
            "AND Sequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)",
            nativeQuery = true)
    void adjustOtherTenantRequestItemGridFieldSequences(
            @Param("privilegeCode") String privilegeCode,
            @Param("tenantId") Integer tenantId,
            @Param("templateId") Integer templateId,
            @Param("currentSequence") Integer currentSequence,
            @Param("newSequence") Integer newSequence);
}
