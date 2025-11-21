package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Purchaser;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
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
public interface PurchaserRepository extends JpaRepository<Purchaser, Integer>, JpaSpecificationExecutor<Purchaser> {

    @Query(value = "SELECT TOP 10 p.* " +
            "FROM CategoryPurchaser cp " +
            "JOIN Purchaser p ON cp.PurchaserId = p.RecId " +
            "WHERE p.TenantId = :tenantId " +
            "AND (:categoryId IS NULL OR cp.CategoryId = :categoryId) " +
            "AND (LOWER(p.PurchaserName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))", nativeQuery = true)
    List<Purchaser> findByTenantIdAndCategoryIdAndCodeOrName(@Param("tenantId") Integer tenantId,
                                                               @Param("categoryId") Integer categoryId,
                                                               @Param("searchTerm") String searchTerm);
    @Query(value = "SELECT * FROM Purchaser WHERE TenantId = :tenantId", nativeQuery = true)
    List<Purchaser> findByTenantId(@Param("tenantId") Integer tenantId);

    @Query(value = "SELECT TOP 1 * FROM Purchaser WHERE TenantId = :tenantId AND LoginId = :loginId ", nativeQuery = true)
    Optional<Purchaser> findByTenantAndLoginId(@Param("tenantId") Integer tenantId, @Param("loginId") String loginId);

    Optional<Purchaser> findPurchaserByTenantAndLoginId(Tenant tenant, String loginId);

    Optional<Purchaser> findPurchaserByTenantAndUserId(Tenant tenant, Integer userId);

    @Query(value = "SELECT TOP 1 * FROM Purchaser WHERE RecId = :purchaserId AND TenantId = :tenantId", nativeQuery = true)
    Optional<Purchaser> findPurchaserByRecIdAndTenant(@Param("purchaserId") Integer purchaserId, @Param("tenantId") Integer tenantId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Purchaser WHERE RecId = :purchaserId ", nativeQuery = true)
    void deletePurchaserByRecId(@Param("purchaserId") Integer purchaserId);

    @Query(value = "SELECT TOP 1 * FROM Purchaser WHERE TenantId = :tenantId AND LoginId = :loginId ", nativeQuery = true)
    Optional<Purchaser> findByLoginId(@Param("tenantId") Integer tenantId, @Param("loginId") String loginId);

    Optional<Purchaser> findFirstByTenantRecIdOrderBySequenceDesc(Integer tenantId);

    Optional<Purchaser> findFirstByRecId(Integer purchaserId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Purchaser SET Sequence = Sequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId AND Sequence != :currentSequence " +
            "AND Sequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void reOrderOtherPurchaserSequence(@Param("tenantId") Integer tenantId,
                                       @Param("currentSequence") Integer currentSequence,
                                       @Param("newSequence") Integer newSequence);
}
