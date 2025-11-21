package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Objective;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ObjectiveRepository extends JpaRepository<Objective, Integer> {

    Objective findObjectiveByRecId(Integer recId);

    @Query(value = "SELECT * FROM Objective WHERE TenantId = :tenantId", nativeQuery = true)
    List<Objective> getObjectiveByTenantId(@Param("tenantId") Integer tenantId);

    @Query(value = "SELECT TOP 10 * FROM Objective WHERE TenantId = :tenantId " +
            "AND (LOWER(Code) LIKE LOWER(CONCAT('%',:searchTerm, '%')) " +
            "OR LOWER(Name) LIKE LOWER(CONCAT('%',:searchTerm, '%')))", nativeQuery = true)
    List<Objective> getObjectiveByTenantIdAndSearchTerm(@Param("tenantId") Integer tenantId,
                                                    @Param("searchTerm") String searchTerm);
    
    Optional<Objective> findByTenantAndCode(Tenant tenant, String code);
}
