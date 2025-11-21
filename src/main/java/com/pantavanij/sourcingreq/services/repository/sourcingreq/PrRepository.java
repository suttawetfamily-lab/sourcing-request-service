package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Pr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrRepository extends JpaRepository<Pr, Integer> {

    @Query(value = "SELECT TOP 1 SUBSTRING(PR.PRNumber, 20,2) FROM PR JOIN RequestItemPR rip on PR.RecId = rip.PRId " +
            "join RequestItem ri on rip.RequestItemId = ri.RecId WHERE ri.RequestId = :requestId AND PR.TenantId = :tenantId ORDER BY PR.RecId DESC", nativeQuery = true)
    Optional<String> findMaxPRNumberByRequestId(@Param("requestId") Long requestId, @Param("tenantId") Integer tenantId);
}
