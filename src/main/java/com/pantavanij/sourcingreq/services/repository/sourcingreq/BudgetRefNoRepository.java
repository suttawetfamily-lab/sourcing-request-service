package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.BudgetRefNo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRefNoRepository extends JpaRepository<BudgetRefNo, Integer> {

    @Query(value = "SELECT TOP 10 * FROM BudgetRefNo WHERE TenantId = :tenantId " +
            "AND (LOWER(Code) LIKE LOWER(CONCAT('%',:searchTerm, '%')) " +
            "OR LOWER(Name) LIKE LOWER(CONCAT('%',:searchTerm, '%')))", nativeQuery = true)
    List<BudgetRefNo> findByTenantIdAndCodeOrName(@Param("tenantId") Integer tenantId,
                                                  @Param("searchTerm") String searchTerm);
    
}
