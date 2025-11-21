// TenantCurrencyOrganizationRepository.java
package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantCurrencyOrganizationRepository
        extends JpaRepository<TenantCurrencyOrganization, TenantCurrencyOrganizationPK> {

    // เอาเฉพาะที่จำเป็นต่อ OptionDto
    interface OptionRow {
        String getValue();     // CurrencyId เป็นสตริงตั้งแต่ DB
        String getCode();      // Currency.Code
        String getName();      // Currency.Name
        Boolean getIsDefault();// จาก TenantCurrency.[Default]
    }

    @Query(value =
            "SELECT " +
                    "  CAST(c.RecId AS varchar(50)) AS value, " +      // ← ให้เป็น String ตั้งแต่ DB
                    "  c.Code  AS code, " +
                    "  c.Name  AS name, " +
                    "  CAST(CASE WHEN ISNULL(tc.[Default], 0) = 1 THEN 1 ELSE 0 END AS bit) AS isDefault " + // ← default ตามของ TenantCurrency
                    "FROM Currency c " +
                    "INNER JOIN TenantCurrencyOrganization tco " +
                    "   ON c.RecId = tco.CurrencyId " +
                    "LEFT JOIN TenantCurrency tc " +
                    "   ON tc.TenantId  = tco.TenantId " +
                    "  AND tc.CurrencyId = tco.CurrencyId " +
                    "WHERE tco.TenantId = :tenantId " +
                    "  AND tco.OrganizationId = :organizationId " +
                    "  AND (LOWER(c.Code) LIKE CONCAT('%', :searchTerm, '%') " +
                    "       OR LOWER(c.Name) LIKE CONCAT('%', :searchTerm, '%')) " +
                    "ORDER BY c.Name",
            nativeQuery = true)
    List<OptionRow> searchCurrencyOptions(@Param("tenantId") Integer tenantId,
                                          @Param("organizationId") Integer organizationId,
                                          @Param("searchTerm") String searchTerm);
}
