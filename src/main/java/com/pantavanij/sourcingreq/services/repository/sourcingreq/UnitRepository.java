package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.*;

import javax.validation.constraints.*;
import java.util.List;
import java.util.Optional;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Integer>, JpaSpecificationExecutor<Unit> {

    @Query(value = "SELECT u.* FROM Unit u " +
            "INNER JOIN TenantUnit tu ON u.RecId = tu.UnitId " +
            "INNER JOIN TenantUnitOrganization tuo ON tu.TenantId = tuo.TenantId AND tu.UnitId = tuo.UnitId " +
            "WHERE tu.Active = 1 AND tu.TenantId = :tenantId AND tuo.OrganizationId = :organizationId ", nativeQuery = true)
    List<Unit> getUnitByTenantId(@Param("tenantId") Integer tenantId, @Param("organizationId") Integer organizationId);

    Unit findByRecId(Integer recId);

    @Query(value = "SELECT u.* FROM Unit u " +
            "INNER JOIN TenantUnit tu ON u.RecId = tu.UnitId " +
            "INNER JOIN TenantUnitOrganization tuo ON tu.TenantId = tuo.TenantId AND tu.UnitId = tuo.UnitId " +
            "WHERE  tu.Active = 1 AND tu.TenantId = :tenantId AND LOWER(u.Code) LIKE LOWER(CONCAT('%',:searchTerm, '%')) AND tuo.OrganizationId = :organizationId ", nativeQuery = true)
    List<Unit> findByTenantIdAndCode(@Param("tenantId") Integer tenantId,
                                     @Param("searchTerm") String searchTerm,
                                     @Param("organizationId") Integer organizationId);

    @Query(value = "SELECT TOP 1 Unit.* FROM Unit WHERE LOWER(Code) = LOWER(:unitCode)", nativeQuery = true)
    Optional<Unit> getUnitByUnitCode(@Param("unitCode") String unitCode);

    @Query(value = "SELECT Unit.* FROM Unit " +
            "INNER JOIN TenantUnit ON Unit.RecId = TenantUnit.UnitId " +
            "WHERE  Active = 1 AND TenantId = :tenantId AND LOWER(Code) = LOWER(:unitCode) ", nativeQuery = true)
    Optional<Unit> getUnitByTenantIdAndUnitCode(@Param("tenantId") Integer tenantId, @Param("unitCode") String unitCode);

    Optional<Unit> findUnitByRecId(Integer unitId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    void deleteByRecId(Integer unitId);
}
