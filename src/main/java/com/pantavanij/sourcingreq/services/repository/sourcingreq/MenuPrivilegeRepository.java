package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Department;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.MenuPrivilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
public interface MenuPrivilegeRepository extends JpaRepository<MenuPrivilege, Integer>, JpaSpecificationExecutor<MenuPrivilege> {

    @Query(value = "SELECT * FROM MenuPrivilege WHERE TypeId IN (1,2,3) AND TenantId = :tenantId ORDER BY RecId ASC", nativeQuery = true)
    List<MenuPrivilege> getMenuPrivilege(@Param("tenantId") Integer tenantId);

    @Query(value = "SELECT * FROM MenuPrivilege WHERE TenantId = :tenantId AND TypeId = :typeId AND PrivilegeCode = :privilegeCode ORDER BY RecId ASC", nativeQuery = true)
    List<MenuPrivilege> findByTenantIdAndTypeIdAndPrivilegeCode(@Param("tenantId") Integer tenantId, @Param("typeId") Integer typeId, @Param("privilegeCode") String privilegeCode);

    Optional<MenuPrivilege> findByTenantRecIdAndRecId(Integer tenantId, Integer menuPrivilegeId);

    Optional<MenuPrivilege> findFirstByTenantRecIdAndMenuNameOrderBySequenceDesc(Integer tenantId, String menuName);

    @Transactional
    @Modifying
    @Query(value = "UPDATE MenuPrivilege SET Sequence = Sequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId AND MenuName =:menuName AND Sequence != :currentSequence " +
            "AND Sequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void reOrderOtherMenuPrivilegeSequence(@Param("tenantId") Integer tenantId,
                                           @Param("menuName") String menuName,
                                        @Param("currentSequence") Integer currentSequence,
                                        @Param("newSequence") Integer newSequence);
}
