package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItem;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SourcingStatus;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Unit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RequestItemRepository extends JpaRepository<RequestItem, Long> {

    @Query(value = "SELECT * from RequestItem WHERE RequestId = :requestId ", nativeQuery = true)
    List<RequestItem> getRequestItemByRequestId(@Param("requestId") Long requestId);

    @Query(value = "SELECT * from RequestItem WHERE RecId IN (:requestItems) ", nativeQuery = true)
    List<RequestItem> getRequestItemByRequestItemIdList(@Param("requestItems") List<Long> requestItems);

    @Query(value = "SELECT * from RequestItem WHERE RequestId = :requestId AND SourcingStatusId IN (1)", nativeQuery = true)
    List<RequestItem> getAllEligibleRequestItemByRequestId(@Param("requestId") Long requestId);

    Page<RequestItem> findByRequest(Request request, Pageable pageable);

    Page<RequestItem> findByRequestAndSourcingType_RecIdAndSourcingDocNo(
            Request request,
            Integer sourcingTypeId,
            String sourcingDocNo,
            Pageable pageable
    );

    RequestItem findRequestItemByRecId(Long requestItemId);
    @Query(value = "SELECT * from RequestItem WHERE RequestId = :requestId AND SourcingStatusId NOT IN (5,9,11,12)", nativeQuery = true)
    List<RequestItem> getSourcingRequestItemByRequestId(@Param("requestId") Long requestId);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "DELETE FROM RequestItem WHERE RecId = :requestItemId ", nativeQuery = true)
    void deleteRequestItemByRecId(@Param("requestItemId") Long requestItemId);

//    @Query(value = "SELECT * from RequestItem WHERE RequestId = :requestId and RecId = :RequestItemId ", nativeQuery = true)
//    RequestItem getRequestItemByRequestIdAndRequestItemId(@Param("requestId") Long requestId,
//                                                          @Param("RequestItemId") Long RequestItemId);

    @Query(value = "SELECT * from RequestItem WHERE SourcingDocNo = :sourcingDocNo and SourcingTypeId = :sourcingTypeId ", nativeQuery = true)
    List<RequestItem> getRequestItemBySourcingDocNoAndSourcingTypeId(@Param("sourcingDocNo") Long sourcingDocNo,
                                                                     @Param("sourcingTypeId") Integer sourcingTypeId);

    @Query(value = "SELECT * FROM RequestItem WHERE RecId = :itemId AND SourcingDocNo = :sourcingDocNo AND SourcingDocId = :sourcingDocId ", nativeQuery = true)
    RequestItem getRequestItemsByCondition(@Param("itemId") Long itemId,
                                           @Param("sourcingDocNo") String sourcingDocNo,
                                           @Param("sourcingDocId") String sourcingDocId);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE RequestItem SET SourcingStatusId = :sourcingStatusId WHERE SourcingDocNo = :sourcingDocNo AND TenantId = :tenantId ", nativeQuery = true)
    void updateRequestItemBySourcingStatus(@Param("sourcingStatusId") Integer sourcingStatusId,
                                           @Param("sourcingDocNo") String sourcingDocNo,
                                           @Param("tenantId") Integer tenantId);

    List<RequestItem> getRequestItemByRequestAndSourcingStatus(Request request, SourcingStatus sourcingStatus);

    Page<RequestItem> findByRequestAndSourcingStatus(Request request, SourcingStatus sourcingStatus, Pageable pageable);

    @Query(value = "SELECT * FROM RequestItem WHERE RecId in (:requestItems) AND RequestId = :requestId AND TenantId = :tenantId ", nativeQuery = true)
    List<RequestItem> getRequestItemByRequestItemsId(@Param("requestId") Long requestId,
                                                     @Param("requestItems") List<Long> requestItems,
                                                     @Param("tenantId") Integer tenantId);

    Optional<RequestItem> findByRequestAndPurposeDescriptionAndItemNameAndItemDescriptionAndUnitAndDeliveryLocationAndContactNameAndPhoneAndBrandAndPartNoAndItemBudget(
            Request request,
            String purposeDesc,
            String itemName,
            String itemNameDesc,
            Unit unit,
            String deliveryLocation,
            String contactName,
            String phone,
            String brand,
            String partNo,
            BigDecimal itemBudget);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE RequestItem SET Quantity = :quantity WHERE RecId = :recId ", nativeQuery = true)
    void updateRequestItemByRecId(@Param("recId") Long recId, @Param("quantity") BigDecimal quantity);

    @Query(value = "SELECT * FROM RequestItem WHERE SourcingDocNo = :sourcingDocNo", nativeQuery = true)
    List<RequestItem> getRequestItemBySourcingDocNo(@Param("sourcingDocNo") String sourcingDocNo);

    @Query(value = "SELECT * FROM RequestItem WHERE SourcingDocNo IN :sourcingDocNoList", nativeQuery = true)
    List<RequestItem> getRequestItemsBySourcingDocNo(@Param("sourcingDocNoList") List<Long> sourcingDocNoList);

    List<RequestItem> findBySourcingDocNo(String sourcingDocNo);

    Page<RequestItem> findByRecIdIn(List<Long> recIds, Pageable pageable);

    @Query("SELECT ri FROM RequestItem ri WHERE ri.request = :request AND ri.sourcingDocNo = :sourcingDocNo")
    Page<RequestItem> findByRequestAndSourcingDocNo(@Param("request") Request request,
                                                    @Param("sourcingDocNo") String sourcingDocNo,
                                                    Pageable pageable);

    @Query(
            value = "SELECT * FROM RequestItem " +
                    "WHERE RequestId = :requestId " +
                    "AND SourcingDocNo = :sourcingDocNo " +
                    "AND SourcingTypeId = :sourcingTypeId",
            nativeQuery = true
    )
    Page<RequestItem> findByRequestAndSourcingDocNoAndSourcingTypeId(
            @Param("requestId") Long requestId,
            @Param("sourcingDocNo") String sourcingDocNo,
            @Param("sourcingTypeId") Integer sourcingTypeId,
            Pageable pageable
    );




}
