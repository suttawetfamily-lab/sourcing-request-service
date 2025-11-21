package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.TenantCurrencyOptionDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Currency;
import com.pantavanij.sourcingreq.services.domain.projection.TenantCurrencyOptionProjection;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.*;

import java.util.*;

@Repository
public interface TenantCurrencyRepository extends JpaRepository<TenantCurrency, Integer>, JpaSpecificationExecutor<TenantCurrency> {

    Optional<TenantCurrency> findByTenantRecIdAndCurrencyRecId(Integer tenantId, Integer currencyRecId);

    Optional<TenantCurrency> findFirstByTenantRecIdOrderBySequenceDesc(Integer tenantId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "UPDATE TenantCurrency SET Sequence = Sequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId AND Sequence != :currentSequence " +
            "AND Sequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void reOrderOtherTenantCurrencySequence(@Param("tenantId") Integer tenantId,
                                            @Param("currentSequence") Integer currentSequence,
                                            @Param("newSequence") Integer newSequence);

    @Query(value = "SELECT c.RecId AS value, c.Code AS code, c.Name AS name, " +
            "tc.Sequence AS sequence, tc.Active AS active, tc.[Default] AS isDefault " +
            "FROM Currency c " +
            "INNER JOIN TenantCurrency tc ON c.RecId = tc.CurrencyId " +
            "WHERE tc.Active = 1 AND tc.TenantId = :tenantId " +
            "AND (LOWER(c.Code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(c.Name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "ORDER BY tc.Sequence", nativeQuery = true)
    List<TenantCurrencyOptionProjection> findByTenantIdAndCodeOrDescription(
            @Param("tenantId") Integer tenantId,
            @Param("searchTerm") String searchTerm);



    @Transactional(rollbackFor = Exception.class)
    @Modifying
    void deleteByTenantRecIdAndCurrencyRecId(Integer recId, Integer recId1);

    // TenantCurrencyRepository.java (เพิ่มเมธอดใหม่ด้านล่าง)
    @Query(value =
            "SELECT " +
        "  CAST(c.RecId AS varchar(50)) AS value, " +
        "  c.Code AS code, " +
                "  c.Name AS name, " +
                "  tc.Sequence AS sequence, " +
                "  tc.Active AS active, " +
                "  tc.[Default] AS isDefault " +
                "FROM Currency c " +
                "INNER JOIN TenantCurrencyOrganization tco " +
                "   ON c.RecId = tco.CurrencyId " +
                "INNER JOIN TenantCurrency tc " +
                "   ON tc.TenantId = tco.TenantId " +
                "  AND tc.CurrencyId = tco.CurrencyId " +
                "WHERE tco.TenantId = :tenantId " +
                "  AND tco.OrganizationId = :organizationId " +
                "  AND tc.Active = 1 " +
                "  AND (LOWER(c.Code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
                "       OR LOWER(c.Name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
                "ORDER BY tc.Sequence",
    nativeQuery = true)
    List<TenantCurrencyOptionProjection> findByTenantIdAndOrganizationIdAndCodeOrDescription(
            @Param("tenantId") Integer tenantId,
            @Param("organizationId") Integer organizationId,
            @Param("searchTerm") String searchTerm);

}
