package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantSourcingGridField;
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
public interface TenantSourcingGridFieldRepository extends JpaRepository<TenantSourcingGridField, Integer>, JpaSpecificationExecutor<TenantSourcingGridField> {

    List<TenantSourcingGridField> findByPrivilegeCodeAndTenant_CodeOrderBySequence(String privilegeCode, String tenantCode);

    List<TenantSourcingGridField> findByPrivilegeCodeAndTenant_CodeAndSearchableOrderBySequence(
            String privilegeCode, String tenantCode, boolean searchable);
    List<TenantSourcingGridField> findByTenant_CodeAndSearchableOrderBySequence(String tenantCode, boolean searchable);

    @Query(value = "SELECT * FROM TenantSourcingGridField WHERE RequestGridFieldId = :requestGridFieldId AND PrivilegeCode = :privilegeCode AND TenantId = :tenantId ", nativeQuery = true)
    Optional<TenantSourcingGridField> findTenantSourcingGridFieldByRecIdAndPrivilegeCodeAndTenantId(@Param("requestGridFieldId") Integer requestGridFieldId, @Param("privilegeCode") String privilegeCode, @Param("tenantId") Integer tenantId);

    @Query(value = "SELECT * FROM TenantSourcingGridField WHERE Code = :code AND PrivilegeCode = :privilegeCode AND TenantId = :tenantId ", nativeQuery = true)
    Optional<TenantSourcingGridField> findTenantSourcingGridFieldByCodeAndPrivilegeCodeAndTenantId(@Param("code") String code, @Param("privilegeCode") String privilegeCode, @Param("tenantId") Integer tenantId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE TenantSourcingGridField SET Sequence = Sequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId AND PrivilegeCode = :privilegeCode AND Sequence != :currentSequence " +
            "AND Sequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void adjustOtherTenantSourcingGridFieldSequences(@Param("privilegeCode") String privilegeCode, @Param("tenantId") Integer tenantId, @Param("currentSequence") Integer currentSequence, @Param("newSequence") Integer newSequence);

    Optional<TenantSourcingGridField> findFirstByTenantRecIdOrderBySequenceDesc(Integer tenantId);
}
