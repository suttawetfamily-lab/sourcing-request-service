package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Supplier;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long>, JpaSpecificationExecutor<Request> {
    Request findRequestsByRecId(Long recId);

    Request findFirstByRequestNo(String requestNo);

    List<Request> findRequestByRecIdIn(List<Long> requestIds);

    List<Request> findRequestsByTenant(Tenant tenant);

    Request findRequestByRecId(Long requestId);

    @Query(value = "SELECT TOP 1 SUBSTRING(RequestNo, 7,6) from Request WHERE TenantId = :tenantId ORDER BY RecId DESC ", nativeQuery = true)
    Optional<String> findLatestRequestNoByTenantId(@Param("tenantId") Integer tenantId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "DELETE FROM Request WHERE RecId = :requestId ", nativeQuery = true)
    void deleteRequestByRecId(@Param("requestId") Long requestId);

    @Query(value = "SELECT * FROM Request WHERE RequestNo = :requestNo AND TenantId = :tenantId", nativeQuery = true)
    Request getRequestByRequestNo(@Param("requestNo") String requestNo, @Param("tenantId") Integer tenantId);

    @Query(value = "declare @sql nvarchar(max) " +
            " declare @condition nvarchar(max) " +
            "set @condition = :condition " +
            "set @sql = " +
            "N'WITH ProjectPrice AS\n" +
            "(\n" +
            "     SELECT DISTINCT epis.ExistingPriceItemId, epis.SupplierId,\n" +
            "          CASE WHEN r.RequestTypeId = 1 AND ri.SourcingStatusId = 9 THEN TRY_CAST(ISNULL(epis.AwardedValue, ri.Quantity) AS DECIMAL(19,4)) * ISNULL(epis.BasePrice, 0)\n" +
            "          ELSE 0 END AS TotalProjectedPrice\n" +
            "          ,CASE WHEN r.RequestTypeId = 1 AND ri.SourcingStatusId = 9 THEN TRY_CAST(ISNULL(epis.AwardedValue, ri.Quantity) AS DECIMAL(19,4)) * ISNULL(epis.BasePrice, 0) * 1.07\n" +
            "          ELSE 0 END AS TotalProjectedPriceVat7Percentage\n" +
            "          ,CASE WHEN r.RequestTypeId = 1 AND ri.SourcingStatusId = 9 THEN TRY_CAST(ISNULL(epis.AwardedValue, ri.Quantity) AS DECIMAL(19,4))\n" +
            "        ELSE TRY_CAST(epis.AwardedValue AS DECIMAL(19,4)) END as AwardedValue\n" +
            "     FROM Request r\n" +
            "     INNER JOIN RequestItem ri ON r.RecId = ri.RequestId\n" +
            "     INNER JOIN ExistingPriceItem epi ON ri.RecId  = epi.RequestItemId\n" +
            "     INNER JOIN ExistingPriceItemSupplier epis ON epi.RecId  = epis.ExistingPriceItemId\n" +
            ")\n" +
            "SELECT DISTINCT r.RequestNo, r.RequestTypeId ,r.RequestName ,ISNULL(rp.ProjectCode, r.ProjectCode) AS ProjectCode ,ISNULL(rp.ProjectName, r.ProjectName) AS ProjectName, rd.DepartmentName, r.BudgetRefNo, r.Budget\n" +
            ",r.RequestDate ,r.ExpectedDate ,r.CreatedBy ,reqS.Description as RequestStatusName ,st.RecId  as SourcingTypeId\n" +
            ",st.Name as SourcingTypeName, ri.SourcingDocNo ,ri.PurposeDescription ,ri.ItemName\n" +
            ",epi.ItemName as ExistingPriceItemName ,ri.ItemDescription ,epi.ItemDescription as ExistingPriceItemDescription\n" +
            ",ISNULL(epi.Brand, ri.Brand) AS Brand, ISNULL(epi.PartNo, ri.PartNo) AS PartNo, ri.Quantity ,ri.Conditions ,u.Code AS UnitCode\n" +
            ",ri.ItemBudget\n" +
            ", CASE WHEN ri.SourcingStatusId = 9 THEN epis.UnitPrice ELSE NULL END AS UnitPrice\n" +
            ",c.Code AS CurrencyCode ,r.VatType\n" +
            ", CASE WHEN ri.SourcingStatusId = 9 THEN epis.SupplierFullName ELSE NULL END as SupplierName\n" +
            ", CASE WHEN ri.SourcingStatusId = 9 THEN epis.SupplierFullName ELSE NULL END as EXSupplierName\n" +
            ",ISNULL(rl.DeliveryLocation, ri.DeliveryLocation) AS DeliveryLocation\n" +
            ",ISNULL(l.Name,l2.Name) as LocationName ,ISNULL(ri.ContactName, rl.ContactName) as ContactName ,ISNULL(rl.Phone, ri.Phone) as Phone\n" +
            ",COALESCE(tc.Name, ric.CategoryName, rc.CategoryName) AS CategoryName\n" +
            ",COALESCE(tsc.Name, risc.SubCategoryName, rsc.SubCategoryName) AS SubCategoryName ,r.AssignedBy\n" +
            ",ss.RecId as sourcingStatusId ,ss.Description as SourcingStatusName , COALESCE(l.RecId, rl.LocationId) as LocationId\n" +
            ",u2.Code as eXUnitCode, ri.RecId as RequestItemId, rt.TypeCode as TypeCode, rt.TypeName\n" +
            ",r.Objective, ro.ObjectiveCode, ro.ObjectiveName\n" +
            ",r.OrganizationId, ria.BidStartDate, ria.BidCompleteDate\n" +
            ",ria.BidCompleteMonth, ria.BidCompleteYear\n" +
            ",ria.BidNo, ria.BiddingType, ria.BidDescription\n" +
            ", CASE WHEN ri.SourcingStatusId = 9 THEN epis.SupplierFullName ELSE NULL END as AwardedVendorName\n" +
            ", epis.TaxId AS TaxNo, ria.VatType as RequestItemAdditionalVatType\n" +
            ",pp.TotalProjectedPrice\n" +
            ",pp.TotalProjectedPriceVat7Percentage\n" +
            ", CASE WHEN r.RequestTypeId = 1 AND ri.SourcingStatusId = 9 THEN (ISNULL(pp.AwardedValue, 0) * epis.UnitPrice ) \n" +
            "     WHEN r.RequestTypeId = 2 AND ri.SourcingStatusId = 9 THEN 0 ELSE NULL END AS TotalFinalPrice\n" +
            ", CASE WHEN r.RequestTypeId = 1 AND ri.SourcingStatusId = 9 THEN (ISNULL(pp.AwardedValue, 0) * epis.UnitPrice * 1.07 ) \n" +
            "     WHEN r.RequestTypeId = 2 AND ri.SourcingStatusId = 9 THEN 0 ELSE NULL END AS TotalFinalPriceVat7Percentage\n" +
            ", CASE WHEN r.RequestTypeId = 1 AND ri.SourcingStatusId = 9 THEN (pp.TotalProjectedPrice - ((ISNULL(pp.AwardedValue, 0) * epis.UnitPrice ))) \n" +
            "     WHEN r.RequestTypeId = 2 AND ri.SourcingStatusId = 9 THEN 0 ELSE NULL END AS TotalSavingAmount\n" +
            ", CASE WHEN r.RequestTypeId = 1 AND ri.SourcingStatusId = 9 THEN ((pp.TotalProjectedPrice - ((ISNULL(pp.AwardedValue, 0) * epis.UnitPrice ))) * 1.07) \n" +
            "     WHEN r.RequestTypeId = 2 AND ri.SourcingStatusId = 9 THEN 0 ELSE NULL END AS TotalSavingAmountVat7Percentage\n" +
            ",ria.CostAvoidanceVat7Percentage\n" +
            ",CASE WHEN ria.TotalProjectedPriceVat7Percentage = 0 THEN 0\n" +
            "     WHEN r.RequestTypeId = 1 AND ri.SourcingStatusId = 9 THEN (ISNULL(ria.CostAvoidanceVat7Percentage,0) + ((pp.TotalProjectedPrice - ((pp.AwardedValue * epis.UnitPrice ))) * 1.07))/(CASE WHEN pp.TotalProjectedPriceVat7Percentage = 0 THEN 1 ELSE pp.TotalProjectedPriceVat7Percentage END)\n" +
            "     WHEN r.RequestTypeId = 2 AND ri.SourcingStatusId = 9 THEN  0 ELSE NULL END AS SavingPercentage\n" +
            ",tc.Name as TenantCategoryName, tsc.Buyer\n" +
            ",ra.Department, ri.BidValidityStartDate, ri.BidValidityEndDate, r.DelegateActionBy, ri.SourcingItemSequence, ri.ItemSequence\n" +
            ",epi.Comment as ExistingPriceComment, epi.vatTypeId\n" +
            ",rs.Withdraw, rs.WithdrawReason, rs.CompanyCode, rs.CompanyName\n" +
            ",CASE WHEN r.RequestTypeId = 1 AND ri.SourcingStatusId = 9 THEN TRY_CAST(pp.AwardedValue AS DECIMAL(19,4))\n" +
            "ELSE NULL END as AwardedQuantity\n" +
            ",CASE\n" +
            "WHEN r.RequestTypeId = 2 AND ri.SourcingStatusId = 9 THEN epis.AwardedValue\n" +
            "ELSE NULL END as AwardedAmount\n" +
            ",CASE WHEN r.RequestTypeId = 1 AND ri.SourcingTypeId = 2 AND ri.SourcingStatusId = 9 THEN ISNULL(TRY_CAST(pp.AwardedValue AS DECIMAL(19,4)), 0) * ISNULL(epis.UnitPrice, 0)\n" +
            "WHEN r.RequestTypeId = 1 AND ri.SourcingTypeId = 3 AND ri.SourcingStatusId = 9 THEN ISNULL(TRY_CAST(pp.AwardedValue AS DECIMAL(19,4)), 0) * ISNULL(epis.UnitPrice, 0)\n" +
            "WHEN r.RequestTypeId = 2 AND ri.SourcingTypeId = 2 AND ri.SourcingStatusId = 9 AND epis.AwardedValue IS NULL THEN NULL\n" +
            "WHEN r.RequestTypeId = 2 AND ri.SourcingTypeId = 2 AND ri.SourcingStatusId = 9 THEN epis.UnitPrice\n" +
            "WHEN r.RequestTypeId = 2 AND ri.SourcingTypeId = 3 AND ri.SourcingStatusId = 9 AND epis.AwardedType = ''AMT'' THEN TRY_CAST(epis.AwardedValue AS DECIMAL(19,4))\n" +
            "WHEN r.RequestTypeId = 2 AND ri.SourcingTypeId = 3 AND ri.SourcingStatusId = 9 AND epis.AwardedType = ''QTY'' THEN ISNULL(TRY_CAST(epis.AwardedValue AS DECIMAL(19,4)), 0) * ISNULL(epis.UnitPrice, 0)\n" +
            "ELSE NULL END as AwardedNetAmount\n" +
            ", CASE WHEN ri.SourcingStatusId = 9 THEN epis.AwardedType ELSE NULL END as AwardedType\n" +
            ", CASE WHEN ri.SourcingStatusId = 9 THEN pp.AwardedValue ELSE NULL END as AwardedValue\n" +
            " \n" +
            "FROM Request r\n" +
            "INNER JOIN RequestItem ri ON r.RecId = ri.RequestId\n" +
            "LEFT JOIN ExistingPriceItem epi ON ri.RecId  = epi.RequestItemId\n" +
            "LEFT JOIN ExistingPriceItemSupplier epis ON epi.RecId  = epis.ExistingPriceItemId\n" +
            "LEFT JOIN ProjectPrice pp ON epis.ExistingPriceItemId  = pp.ExistingPriceItemId AND epis.SupplierId = pp.SupplierId\n" +
            "LEFT JOIN Unit u ON ri.UnitId  = u.RecId\n" +
            "LEFT JOIN Unit u2 ON epi.UnitId = u2.RecId   \n" +
            "LEFT JOIN Currency c ON epi.CurrencyId = c.RecId\n" +
            "LEFT JOIN RequestStatus reqS ON  r.StatusId = reqS.RecId\n" +
            "LEFT JOIN RequestLocation rl ON r.RecId  = rl.RequestId\n" +
            "LEFT JOIN Location l2 ON rl.LocationId = l2.RecId  \n" +
            "LEFT JOIN RequestItemLocation ril ON ri.RecId  = ril.RequestItemId\n" +
            "LEFT JOIN Location l ON ril.LocationId = l.RecId  \n" +
            "LEFT JOIN SourcingType st ON st.RecId = ri.SourcingTypeId\n" +
            "LEFT JOIN SourcingStatus ss ON ss.RecId = ri.SourcingStatusId\n" +
            "LEFT JOIN RequestType rt ON rt.RequestId = r.RecId\n" +
            "LEFT JOIN Type t ON t.RecId = rt.TypeId\n" +
            "LEFT JOIN RequestObjective ro ON   ro.RequestId =r.RecId\n" +
            "LEFT JOIN Objective o ON ro.ObjectiveId = o.RecId\n" +
            "LEFT JOIN RequestProject rp ON r.RecId = rp.RequestId\n" +
            "LEFT JOIN RequestDepartment rd ON r.RecId = rd.RequestId\n" +
            "LEFT JOIN RequestCategory rc ON r.RecId = rc.RequestId\n" +
            "LEFT JOIN RequestSubCategory rsc ON r.RecId = rsc.RequestId\n" +
            "LEFT JOIN RequestItemCategory ric ON ri.RecId = ric.RequestItemId\n" +
            "LEFT JOIN RequestItemSubCategory risc ON ri.RecId = risc.RequestItemId\n" +
            "LEFT JOIN RequestItemAdditional ria ON ri.RecId = ria.RequestItemId\n" +
            "LEFT JOIN TenantSubCategory tsc ON tsc.RecId = risc.SubCategoryId\n" +
            "LEFT JOIN TenantCategory tc ON tc.RecId = tsc.CategoryId\n" +
            "LEFT JOIN RequestAdditional ra ON ra.RequestId = r.RecId\n" +
            "LEFT JOIN RequestSourcing rs ON rs.RequestId = r.RecId AND rs.SourcingDocNo = ri.SourcingDocNo\n" +
            "WHERE 1=1 ' + @condition  exec (@sql)"
            , nativeQuery = true

    )
    List<Object[]> getExcelDataByCondition(@Param("condition") String condition);

    @Query(value = "SELECT RequestId FROM (SELECT DISTINCT TOP 500 ri.RequestId, ri.SourcingDocNo, ri.UpdatedDate FROM RequestItem ri WHERE ri.TenantId = :tenantId AND ri.SourcingTypeId = 3 AND ri.SourcingDocNo IS NOT NULL AND ri.UpdatedDate <= DATEADD(MINUTE, -2, GETDATE())) t ORDER BY t.UpdatedDate ASC", nativeQuery = true)
    List<Long> getTopNLatestERFXRequest(@Param("tenantId") Integer tenantId);

    @Query(value = "SELECT COUNT(DISTINCT r.RecId) FROM Request r " +
            "INNER JOIN RequestApprover ra  ON r.RecId = ra.RequestId " +
            "INNER JOIN Approver a ON ra.ApproverId = a.RecId " +
            "WHERE r.TenantId = :tenantId " +
            "AND ra.DeptApprovalStatusId = :statusId " +
            "AND a.LoginId = :username ", nativeQuery = true)
    Integer countDeptApproverTask(@Param("tenantId") Integer tenantId, @Param("username") String username, @Param("statusId") Integer statusId);

    @Query(value = "SELECT COUNT(DISTINCT r.RecId) FROM Request r " +
            "WHERE r.TenantId = :tenantId " +
            "AND r.ApprovalStatusId = :approvalStatusId " +
            "AND r.DelegateActionBy IS NULL " +
            "AND r.AssignedBy = :username ", nativeQuery = true)
    Integer countPurchaserTask(@Param("tenantId") Integer tenantId, @Param("username") String username, @Param("approvalStatusId") Integer approvalStatusId);

//    @Query(value = "SELECT COUNT(DISTINCT r.RecId) FROM Request r " +
//            "INNER JOIN RequestReviewer rr ON r.RecId = rr.RequestId " +
//            "WHERE r.TenantId = :tenantId " +
//            "AND r.StatusId NOT IN (1,6) " +
//            "AND rr.ReviewerName = :username " +
//            "AND rr.[Read] = 0 ", nativeQuery = true)
//    Integer countReviewerTask(@Param("tenantId") Integer tenantId, @Param("username") String username);
//
//    @Query(value = "SELECT COUNT(DISTINCT r.RecId) FROM Request r " +
//            "INNER JOIN RequestReportLine rrl ON r.RecId = rrl.RequestId " +
//            "INNER JOIN ReportLine rl ON rl.RecId = rrl.ReportLineId " +
//            "WHERE r.TenantId = :tenantId " +
//            "AND r.StatusId NOT IN (1,6) " +
//            "AND rl.LoginId = :username " +
//            "AND rrl.[Read] = 0 ", nativeQuery = true)
//    Integer countReportLineTask(@Param("tenantId") Integer tenantId, @Param("username") String username);

    @Query(value = "SELECT COUNT(DISTINCT r.recId) FROM Request r " +
            "JOIN " +
            "( " +
            " SELECT rr.RequestId , rv.LoginId AS LoginId, rr.[read] FROM RequestReviewer rr " +
            " JOIN Reviewer rv on rr.ReviewerId = rv.RecId" +
            " " +
            " UNION " +
            " " +
            " SELECT rrl.RequestId ,rl.LoginId, rrl.[read] FROM RequestReportLine rrl " +
            " JOIN ReportLine rl ON rrl.ReportLineId =rl.RecId " +
            ") t ON r.recId = t.requestId " +
            "WHERE r.tenantId = :tenantId " +
            "AND r.StatusId NOT IN (1,6) " +
            "AND t.LoginId = :username " +
            "AND t.[read] = 0 ", nativeQuery = true)
    Integer countReviewerAndReportLineTask(@Param("tenantId") Integer tenantId, @Param("username") String username);



    @Query(value = "SELECT DISTINCT r.*\n" +
            "FROM Request r\n" +
            "JOIN RequestItem ri ON r.RecId = ri.RequestId\n" +
            "JOIN ExistingPriceItem epi ON ri.RecId = epi.RequestItemId\n" +
            "JOIN ExistingPriceItemSupplier epis ON epi.RecId = epis.ExistingPriceItemId\n" +
            "WHERE r.TenantId = :tenantId AND r.StatusId = 4 AND r.ApprovalStatusId = 4 --Completed \n" +
            "AND ri.SourcingStatusId  IN (9) --Qualified\n" +
            "AND ( :applyOrgFilter = 0 OR r.OrganizationId IN (:organizationIds) ) \n" +
            "AND r.IsCopyToPRFailed IS NULL\n" +
            "AND r.RecId NOT IN \n" +
            "(\n" +
            "\tSELECT DISTINCT  r.RecId\n" +
            "\tFROM Request r\n" +
            "\tJOIN RequestItem ri ON r.RecId = ri.RequestId\n" +
            "\tJOIN ExistingPriceItem epi ON ri.RecId = epi.RequestItemId\n" +
            "\tJOIN ExistingPriceItemSupplier epis ON epi.RecId = epis.ExistingPriceItemId\n" +
            "\tJOIN Supplier s ON epis.SupplierId = s.RecId\n" +
            "\n" +
            "\tWHERE r.TenantId = :tenantId AND r.StatusId = 4 AND r.ApprovalStatusId = 4 --Completed \n" +
            "\tAND ri.SourcingStatusId  IN (9) --Qualified\n" +
            "\tAND ( :applyOrgFilter = 0 OR r.OrganizationId IN (:organizationIds) ) \n" +
            "\tAND s.ActiveOnERP = 0\n" +
            "\tAND s.TaxId IS NULL\n" +
            ")" +
            "ORDER BY RecId ", nativeQuery = true)
    List<Request> getERPRequestByTenantAndSourcingStatus(@Param("tenantId") Integer tenantId,
                                                           @Param("applyOrgFilter") int applyOrgFilter,
                                                           @Param("organizationIds") List<Integer> organizationIds);


}