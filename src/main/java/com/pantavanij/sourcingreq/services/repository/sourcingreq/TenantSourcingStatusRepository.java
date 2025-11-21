package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantSourcingStatus;
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
public interface TenantSourcingStatusRepository extends JpaRepository<TenantSourcingStatus, Integer>, JpaSpecificationExecutor<TenantSourcingStatus> {


    Optional<TenantSourcingStatus> findById_SourcingStatusIdAndTenant(Integer sourcingStatusId, Tenant tenant);
    void deleteById_SourcingStatusIdAndTenant(Integer sourcingStatusId, Tenant tenant);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "UPDATE TenantSourcingStatus SET Sequence = Sequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId AND Sequence != :currentSequence " +
            "AND Sequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void reOrderOtherTenantSourcingStatusSequence(@Param("tenantId") Integer tenantId,
                                            @Param("currentSequence") Integer currentSequence,
                                            @Param("newSequence") Integer newSequence);

}
