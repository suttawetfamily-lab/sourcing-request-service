package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExistingPriceItem;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItem;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExistingPriceItemRepository extends JpaRepository<ExistingPriceItem, Long> {
    ExistingPriceItem findExistingPriceItemByRecId(Long existingPriceItemId);

    ExistingPriceItem findExistingPriceItemByRequestItem(RequestItem requestItem);


    @Query("SELECT e FROM ExistingPriceItem e " +
            "WHERE e.requestItem = :requestItem " +
            "AND ( " +
            "      (:sourcingDocNo IS NOT NULL AND e.sourcingDocNo = :sourcingDocNo) " +
            "   OR (:sourcingDocNo IS NULL AND e.sourcingDocNo IS NULL) " +
            ")")
    ExistingPriceItem findByRequestItemAndNullableSourcingDocNo(
            @Param("requestItem") RequestItem requestItem,
            @Param("sourcingDocNo") String sourcingDocNo);



    @Query(value =
            "SELECT TOP 1 e.* " +
                    "FROM ExistingPriceItem e " +
                    "WHERE e.RequestItemId = :requestItemId " +
                    "AND ( " +
                    "      (:sourcingDocNo IS NOT NULL AND e.SourcingDocNo = :sourcingDocNo) " +
                    "   OR (:sourcingDocNo IS NULL AND e.SourcingDocNo IS NULL) " +
                    ") " +
                    "ORDER BY e.RecId DESC",
            nativeQuery = true)
    Optional<ExistingPriceItem> findExistingPriceItemByRequestItemIdAndNullableSourcingDocNo(
            @Param("requestItemId") Long requestItemId,
            @Param("sourcingDocNo") String sourcingDocNo);



    List<ExistingPriceItem> findExistingPriceItemsByRequestIn(List<Request> requestItemList);

    void deleteExistingPriceItemByRecId(Long existingPriceItemId);

    @Query(value =
            "SELECT TOP 1 e.* " +
                    "FROM ExistingPriceItem e " +
                    "WHERE e.RequestId = :requestId " +
                    "AND e.RequestItemId = :requestItemId " +
                    "AND e.TenantId = :tenantId " +
                    "AND ( " +
                    "      (:sourcingDocNo IS NOT NULL AND e.SourcingDocNo = :sourcingDocNo) " +
                    "   OR (:sourcingDocNo IS NULL AND e.SourcingDocNo IS NULL) " +
                    ") " +
                    "ORDER BY e.RecId DESC",
            nativeQuery = true)
    ExistingPriceItem getExistingPriceItemByRequestIdAndRequestItemIdAndNullableSourcingDocNo(
            @Param("requestId") Long requestId,
            @Param("requestItemId") Long requestItemId,
            @Param("tenantId") Integer tenantId,
            @Param("sourcingDocNo") String sourcingDocNo);



    List<ExistingPriceItem> findByRequestItemIn(List<RequestItem> requestItems);

    List<ExistingPriceItem> findByTenantAndRequestItemIn(Tenant tenant, List<RequestItem> requestItems);

    Optional<ExistingPriceItem> findByRequestItem(RequestItem requestItem);


    @Query("select e.recId from ExistingPriceItem e " +
            "where e.sourcingTypeId = :sourcingTypeId and e.sourcingDocNo = :sourcingDocNo")
    List<Long> findIdsBySourcingTypeIdAndSourcingDocNo(@Param("sourcingTypeId") Integer sourcingTypeId,
                                                       @Param("sourcingDocNo") String sourcingDocNo);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from ExistingPriceItem e " +
            "where e.sourcingTypeId = :sourcingTypeId and e.sourcingDocNo = :sourcingDocNo")
    void deleteBySourcingTypeIdAndSourcingDocNo(@Param("sourcingTypeId") Integer sourcingTypeId,
                                                @Param("sourcingDocNo") String sourcingDocNo);

    @Query("SELECT e FROM ExistingPriceItem e " +
            "WHERE e.tenant = :tenant " +
            "AND e.sourcingTypeId = :sourcingTypeId " +
            "AND e.sourcingDocNo = :sourcingDocNo")
    List<ExistingPriceItem> findByTenantAndSourcingTypeIdAndSourcingDocNo(
            @Param("tenant") Tenant tenant,
            @Param("sourcingTypeId") Integer sourcingTypeId,
            @Param("sourcingDocNo") String sourcingDocNo);

    @Query("SELECT epi FROM ExistingPriceItem epi " +
            "WHERE epi.request = :request " +
            "AND epi.sourcingDocNo = :sourcingDocNo " +
            "AND epi.sourcingTypeId = :sourcingTypeId")
    List<ExistingPriceItem> findByRequestAndSourcingDocNoAndSourcingTypeId(
            @Param("request") Request request,
            @Param("sourcingDocNo") String sourcingDocNo,
            @Param("sourcingTypeId") Integer sourcingTypeId);


    @Query("SELECT epi FROM ExistingPriceItem epi " +
            "WHERE epi.request = :request " +
            "AND epi.sourcingDocNo = :sourcingDocNo")
    List<ExistingPriceItem> findByRequestAndSourcingDocNo(
            @Param("request") Request request,
            @Param("sourcingDocNo") String sourcingDocNo);



}
