package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Type;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface TypeRepository extends JpaRepository<Type, Integer>, JpaSpecificationExecutor<Type> {

    Type findTypeByRecId(Integer recId);

    @Query(value = "SELECT * FROM Type WHERE TenantId = :tenantId AND [Active] = 1 ", nativeQuery = true)
    List<Type> getTypeByTenantId(@Param("tenantId") Integer tenantId);

    @Query(value = "SELECT TOP 10 * FROM Type WHERE TenantId = :tenantId AND (LOWER(Code) LIKE LOWER(CONCAT('%',:searchTerm, '%')) OR LOWER(Name) LIKE LOWER(CONCAT('%',:searchTerm, '%')))", nativeQuery = true)
    List<Type> getTypeByTenantIdAndSearchTerm(@Param("tenantId") Integer tenantId,
                                                    @Param("searchTerm") String searchTerm);
    
    Optional<Type> findByTenantAndCode(Tenant tenant, String code);
    Optional<Type> findByTenantRecIdAndRecId(Integer tenantId, Integer recId);
    Optional<Type> findFirstByTenantRecIdOrderBySequenceDesc(Integer tenantId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Type SET Sequence = Sequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId AND Sequence != :currentSequence " +
            "AND Sequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void reOrderOtherTypeSequence(@Param("tenantId") Integer tenantId,
                                  @Param("currentSequence") Integer currentSequence,
                                  @Param("newSequence") Integer newSequence);
}
