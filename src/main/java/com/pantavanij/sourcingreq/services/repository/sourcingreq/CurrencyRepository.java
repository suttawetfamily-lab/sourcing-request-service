package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Currency;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Integer>, JpaSpecificationExecutor<Currency> {
    Currency findCurrenciesByRecId(Integer recId);
    Optional<Currency> findCurrencyByRecId(Integer recId);

    @Query(value = "SELECT Currency.* FROM Currency " +
            "INNER JOIN TenantCurrency ON Currency.RecId = TenantCurrency.CurrencyId " +
            "WHERE Active = 1 AND TenantCurrency.TenantId = :tenantId", nativeQuery = true)
    List<Currency> findByTenantId(@Param("tenantId") Integer tenantId);

    @Query(value = "SELECT Currency.* FROM Currency " +
            "INNER JOIN TenantCurrency ON Currency.RecId = TenantCurrency.CurrencyId " +
            "WHERE Active = 1 AND TenantCurrency.TenantId = :tenantId " +
            "AND (LOWER(Currency.Code) LIKE LOWER(CONCAT('%',:searchTerm, '%')) " +
            "OR LOWER(Currency.Name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) ORDER BY Currency.Code"
            , nativeQuery = true)
    List<Currency> findByTenantIdAndCodeOrDescription(@Param("tenantId") Integer tenantId,
                                                      @Param("searchTerm") String searchTerm);

    Optional<Currency> findByRecId(Integer currencyId);
    Optional<Currency> findByCode(String code);
    void deleteByRecId(Integer currencyId);
}
