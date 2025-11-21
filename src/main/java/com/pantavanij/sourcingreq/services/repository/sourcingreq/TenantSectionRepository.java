package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.*;

import java.util.*;

@Repository
public interface TenantSectionRepository extends JpaRepository<TenantSection, Long>, JpaSpecificationExecutor<TenantSection> {

    TenantSection findTenantSectionById(Long id);

    @Query(value = "SELECT * FROM TenantSection WHERE Visible = 1 AND TenantId = :tenantId AND Type IN :types  ORDER BY Sequence ", nativeQuery = true)
    List<TenantSection> getTenantSectionByTenantIdAndType(@Param("tenantId") Integer tenantId,@Param("types")  List<String> types);

    @Query(value = "SELECT * FROM TenantSection WHERE RecId = :recId ORDER BY Sequence ", nativeQuery = true)
    Optional<TenantSection> findByRecId(@Param("recId") Integer recId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "UPDATE TenantSection SET Sequence = Sequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId AND Sequence != :currentSequence " +
            "AND Sequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void reOrderOtherTenantSectionSequence(@Param("tenantId") Integer tenantId, @Param("currentSequence") Integer currentSequence, @Param("newSequence") Integer newSequence);

    Optional<TenantSection> findFirstByTenantRecIdOrderBySequenceDesc(Integer tenantId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE TenantSection SET Sequence = Sequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId AND Sequence != :currentSequence " +
            "AND Sequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void reOrderOtherReportLineSequence(@Param("tenantId") Integer tenantId,
                                        @Param("currentSequence") Integer currentSequence,
                                        @Param("newSequence") Integer newSequence);

    List<TenantSection> findByTenantAndTypeIn(Tenant tenant, List<String> types);

    @Query(value = "SELECT * " +
            "FROM TenantSection ts " +
            "WHERE ts.TenantId = :tenantId " +
            "AND ts.visible = 1 " +
            "AND ts.TemplateId = :templateId " +
            "AND ts.Type IN (:types) " +
            "ORDER BY Sequence ",
            nativeQuery = true)
    List<TenantSection> getTenantSectionByTenantIdAndTemplateIdAndType(
            @Param("tenantId") Integer tenantId,
            @Param("templateId") Integer templateId,
            @Param("types") List<String> types);


    List<TenantSection> findByTenantAndTemplateAndTypeIn(Tenant tenant, TenantTemplate tenantTemplate, List<String> types);

}
