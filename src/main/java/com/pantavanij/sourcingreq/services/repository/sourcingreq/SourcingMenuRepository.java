package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SourcingMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SourcingMenuRepository extends JpaRepository<SourcingMenu, Integer> {

    @Query(value = "SELECT SourcingMenu.* FROM SourcingMenu " +
                   "INNER JOIN TenantSourcingMenu ON SourcingMenu.RecId = TenantSourcingMenu.SourcingMenuId " +
                   "WHERE Visibled = 1 AND TenantSourcingMenu.TenantId = :tenantId", nativeQuery = true)
    List<SourcingMenu> getSourcingMenusByByTenantId(@Param("tenantId") Integer tenantId);

}
