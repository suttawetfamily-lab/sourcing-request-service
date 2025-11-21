package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExistingPriceItem;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExistingPriceItemSupplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExistingPriceItemSupplierRepository extends JpaRepository<ExistingPriceItemSupplier, Integer> {

    List<ExistingPriceItemSupplier> findExistingPriceItemSupplierByExistingPriceItem(ExistingPriceItem existingPriceItem);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "INSERT INTO ExistingPriceItemSupplier(" +
                "ExistingPriceItemId, " +
                "SupplierId, " +
                "SupplierShortName, " +
                "SupplierFullName, " +
                "TaxId, " +
                "AwardedType, " +
                "AwardedValue, " +
                "UnitPrice, " +
                "CreatedBy, " +
                "CreatedDate" +
            ") VALUES (" +
                ":existingPriceItemId, " +
                ":supplierId, " +
                ":supplierShortName, " +
                ":supplierFullName, " +
                ":taxId, " +
                ":awardedType, " +
                ":awardedValue, " +
                ":unitPrice, " +
                ":createdBy, " +
                ":createdDate" +
            ")", nativeQuery = true)
    void saveExistingPriceItemSupplier(
            @Param("existingPriceItemId") Long existingPriceItemId,
            @Param("supplierId") Integer supplierId,
            @Param("supplierShortName") String supplierShortName,
            @Param("supplierFullName") String supplierFullName,
            @Param("taxId") String taxId,
            @Param("awardedType") String awardedType,
            @Param("awardedValue") String awardedValue,
            @Param("unitPrice") BigDecimal unitPrice,
            @Param("createdBy") String createdBy,
            @Param("createdDate") Timestamp createdDate);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "DELETE FROM ExistingPriceItemSupplier WHERE ExistingPriceItemId = :existingPriceItemId ", nativeQuery = true)
    void deleteExistingPriceItemSupplierByExistingPriceItemId(@Param("existingPriceItemId") Long existingPriceItemId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    void deleteByExistingPriceItem(ExistingPriceItem existingPriceItem);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "DELETE FROM ExistingPriceItemSupplier WHERE ExistingPriceItemId = :existingPriceItemId AND SupplierId = :supplierId ", nativeQuery = true)
    void deleteExistingPriceItemSupplierByExistingPriceItemIdAndSupplierId(@Param("existingPriceItemId") Long existingPriceItemId, @Param("supplierId") Integer supplierId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "SELECT epis.* FROM ExistingPriceItem epi JOIN ExistingPriceItemSupplier epis ON epi.RecId = epis.ExistingPriceItemId WHERE epi.RequestId = :requestId ", nativeQuery = true)
    List<ExistingPriceItemSupplier> findExistingPriceItemSupplierByRequest(@Param("requestId") Long requestId);

    Optional<ExistingPriceItemSupplier> findFirstByExistingPriceItemRecId(Long existingPriceItemRecId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    void deleteByExistingPriceItemRecIdIn(List<Long> existingPriceItemRecIds);
}
