package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantConfigRepository extends JpaRepository<TenantConfig, Integer>, JpaSpecificationExecutor<TenantConfig> {

    @Query(value = "SELECT [Value] FROM TenantConfig " +
            "WHERE TenantId = :tenantId AND Topic = :topic AND Section = :section AND Name = :name ", nativeQuery = true)
    String getValueByTenantIdAndTopicAndSectionAndName(@Param("tenantId") Integer tenantId,
                                                       @Param("topic") String topic,
                                                       @Param("section") String section,
                                                       @Param("name") String name);

    @Query(value = "SELECT TOP 1 * FROM TenantConfig " +
            "WHERE TenantId = :tenantId AND Topic = :topic AND Section = :section AND Name = :name ", nativeQuery = true)
    Optional<TenantConfig> getByTenantIdAndTopicAndSectionAndName(@Param("tenantId") Integer tenantId,
                                                                  @Param("topic") String topic,
                                                                  @Param("section") String section,
                                                                  @Param("name") String name);

    @Query(value = "SELECT * FROM TenantConfig " +
            "WHERE TenantId = :tenantId AND Topic = :topic AND Section = :section ORDER BY Sequence", nativeQuery = true)
    List<TenantConfig> findByTenantIdAndTopicAndSection(@Param("tenantId") Integer tenantId,
                                                        @Param("topic") String topic,
                                                        @Param("section") String section);

    Optional<TenantConfig> findByRecIdAndTenant(Integer tenantConfigId, Tenant tenant);

    void deleteByRecIdAndTenant(Integer tenantConfigId, Tenant tenant);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "UPDATE TenantConfig SET Sequence = Sequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId AND Sequence != :currentSequence " +
            "AND Sequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void reOrderOtherTenantConfigSequence(@Param("tenantId") Integer tenantId,
                                          @Param("currentSequence") Integer currentSequence,
                                          @Param("newSequence") Integer newSequence);

    Optional<TenantConfig> findFirstByTenantRecIdOrderBySequenceDesc(Integer tenantId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value =
        "WITH CTE AS ( " +
        "   SELECT " +
        "       * " +
        "       ,ROW_NUMBER() OVER (ORDER BY sequence) AS new_sequence " +
        "   FROM TenantConfig " +
        "   WHERE TenantId = :tenantId) " +
        "UPDATE CTE " +
        "SET sequence = new_sequence " +
        "WHERE TenantId = :tenantId ",nativeQuery = true)
    void reOrderSequenceByTenantRecId(@Param("tenantId") Integer tenantId);

}
