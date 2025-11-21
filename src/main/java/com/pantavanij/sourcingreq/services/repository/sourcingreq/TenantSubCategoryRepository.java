package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantSubCategoryRepository extends JpaRepository<TenantSubCategory, Long> {

    @Query(value = "SELECT * FROM TenantSubCategory tsc " +
            "JOIN TenantCategory tc ON tsc.CategoryId = tc.RecId " +
            "WHERE tc.TenantId = :tenantId AND tsc.IsActive = 1 " +
            "AND tc.TypeId = :typeId and tsc.Buyer like %:buyer% ",
            nativeQuery = true)
    List<TenantSubCategory> getSubcategoryByTenantIdAndTypeId(@Param("tenantId") Integer tenantId, @Param("typeId") Integer typeId, @Param("buyer") String buyer);

    @Query(value = "SELECT * FROM TenantSubCategory tsc " +
            "JOIN TenantCategory tc ON tsc.CategoryId = tc.RecId " +
            "WHERE tc.TenantId = :tenantId AND tc.TypeId = :typeId AND tsc.IsActive = 1 ",
            nativeQuery = true)
    List<TenantSubCategory> getSubcategoryByTenantId(@Param("tenantId") Integer tenantId, @Param("typeId") Integer typeId);

    @Query(value = "SELECT TOP 1 tsc.* FROM TenantSubCategory tsc " +
            "JOIN TenantCategory tc ON tsc.CategoryId = tc.RecId " +
            "JOIN Type t ON tc.TypeId = t.RecId " +
            "WHERE tc.TenantId = :tenantId AND tsc.IsActive = 1 " +
            "AND t.Name = :type " +
            "AND tc.Name = :category " +
            "AND tsc.Name = :subCategory ",
            nativeQuery = true)
    Optional<TenantSubCategory> getSubCategoriesByTenantIdAndTypeAndCategory(@Param("tenantId") Integer tenantId, @Param("type") String type, @Param("category") String category, @Param("subCategory") String subCategory);

}
