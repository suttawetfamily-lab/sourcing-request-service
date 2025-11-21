package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.dto.approver.ApproverRequestDto;
import com.pantavanij.sourcingreq.services.domain.dto.deptapprover.DeptApproverRequestDto;
import com.pantavanij.sourcingreq.services.domain.dto.requester.RequesterRequestDto;
import com.pantavanij.sourcingreq.services.domain.dto.requester.RequesterRequestSearchDataDto;
import com.pantavanij.sourcingreq.services.domain.dto.reviewer.ReviewerRequestDto;
import com.pantavanij.sourcingreq.services.domain.dto.sourcingapprover.SourcingApproverRequestDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


import java.util.ArrayList;
import java.util.List;

import static com.pantavanij.sourcingreq.services.enums.SourcingStatus.SOURCING_QUALIFIED_SUPPLIER;
import static com.pantavanij.sourcingreq.services.enums.TenantRequestStatus.TENANT_REQUEST_COMPLETED;

@Mapper(uses = {RequestStatusMapper.class, ApprovalStatusMapper.class, RequestItemMapper.class, RequestApproverMapper.class},
        imports = DateTimeUtil.class)
public interface RequestMapper {

    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

    RequestDto toRequestDto(Request request);

    ApproverRequestDto toApproverRequestDto(Request request);

    RequesterRequestDto toRequesterRequestDto(Request request);

    ReviewerRequestDto toReviewerRequestDto(Request request);

    DeptApproverRequestDto toDeptApproverRequestDto(Request request);

    SourcingApproverRequestDto toSourcingApproverRequestDto(Request request);

    RequesterRequestSearchDataDto toRequesterRequestSearchDataDto(RequesterRequestDto requesterRequestDto);

    List<RequesterRequestSearchDataDto> toRequesterRequestSearchDataDto(List<RequesterRequestDto> requesterRequestDtos);

    ReportLineDto toReportLineDto(ReportLine reportLine);

    RequesterDto toRequesterDto(Requester requester);

    ReviewerDto toReviewerDto(Reviewer reviewer);

    ApproverDto toApproverDto(Approver approver);

    PurchaserDto toPurchaserDto(Purchaser purchaser);

    ProjectDto toProjectDto(Project project);

    DepartmentDto toDepartmentDto(Department department);

    ProjectChangeLogHeaderDto toProjectChangeLogHeaderDto(ProjectChangeLogHeader projectChangeLogHeader);

    CategoryPurchaserDto toCategoryPurchaserDto(CategoryPurchaser categoryPurchaser);

    PurchaserCategoryDto toPurchaserCategoryDto(CategoryPurchaser categoryPurchaser);

    PurchaserSubCategoryDto toPurchaserSubCategoryDto(SubCategoryPurchaser subCategoryPurchaser);

    default RequestDto toRequestDto(Request request, String timeZone) {
        RequestDto requestDto = toRequestDto(request);
        if (requestDto != null) {
            requestDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requestDto.getCreatedDate(), timeZone));
            requestDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requestDto.getUpdatedDate(), timeZone));
            // Comment this code for PTVNBAY-1797 No concern timezone use only date for expected date.
//            requestDto.setExpectedDate(
//                    DateTimeUtil.convertTimestampByUserTimeZone(requestDto.getExpectedDate(), timeZone));
            requestDto.setRequestDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requestDto.getRequestDate(), timeZone));
            requestDto.setApprovalDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requestDto.getApprovalDate(), timeZone));
            requestDto.setRequestStatus(
                    RequestStatusMapper.INSTANCE.toRequestStatusDto(request.getRequestStatus(), timeZone));
            requestDto.setApprovalStatus(
                    ApprovalStatusMapper.INSTANCE.toApprovalStatusDto(request.getApprovalStatus(), timeZone));
            requestDto.setRequestItemList(
                    RequestItemMapper.INSTANCE.toRequestItemV2DtoList(request.getRequestItemList(), timeZone));
            requestDto.setCurrencyObj(
                    CurrencyMapper.INSTANCE.currencyToCurrencyDto(request.getCurrency()));
        }
        return requestDto;
    }

    default ApproverRequestDto toApproverRequestDto(Request request, String timeZone, List<String> copyToPrPrivilegeCodes,Boolean forceSelectAllItemCopyToPR, List<String> copyToPrOrganizations) {
        ApproverRequestDto approverRequestDto = toApproverRequestDto(request);
        if (approverRequestDto != null) {
            approverRequestDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(approverRequestDto.getCreatedDate(), timeZone));
            approverRequestDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(approverRequestDto.getUpdatedDate(), timeZone));
            approverRequestDto.setExpectedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(approverRequestDto.getExpectedDate(), timeZone));
            approverRequestDto.setRequestDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(approverRequestDto.getRequestDate(), timeZone));
            approverRequestDto.setApprovalDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(approverRequestDto.getApprovalDate(), timeZone));
            approverRequestDto.setRequestStatus(
                    RequestStatusMapper.INSTANCE.toRequestStatusDto(request.getRequestStatus(), timeZone));
            approverRequestDto.setApprovalStatus(
                    ApprovalStatusMapper.INSTANCE.toApprovalStatusDto(request.getApprovalStatus(), timeZone));

            boolean canCopyToPr = false;

            if (approverRequestDto.getApprovalStatus() != null && approverRequestDto.getApprovalStatus().isCanCopyToPR()) {

                // ตรวจสิทธิ์จาก privilegeCode
                boolean canCopyToPrByPrivilegeCode = AppUtil.checkCanCopyToPRByPrivilegeCode(copyToPrPrivilegeCodes);

                // ตรวจสิทธิ์จาก organizationId
                boolean canCopyToPrByOrganizations = true; // default = true ถ้าไม่มี list ให้ข้ามการเช็ค
                if (copyToPrOrganizations != null && !copyToPrOrganizations.isEmpty()) {
                    canCopyToPrByOrganizations = request.getOrganizationId() != null
                            && copyToPrOrganizations.stream()
                            .anyMatch(org -> org.equalsIgnoreCase(String.valueOf(request.getOrganizationId())));
                }

                canCopyToPr = canCopyToPrByPrivilegeCode && canCopyToPrByOrganizations;

                if(forceSelectAllItemCopyToPR){
                    boolean isRequestCompleted = request.getRequestStatus() != null
                            && TENANT_REQUEST_COMPLETED.code().equalsIgnoreCase(request.getRequestStatus().getName());

                    boolean allItemsQualified = request.getRequestItemList() != null
                            && !request.getRequestItemList().isEmpty()
                            && request.getRequestItemList().stream()
                            .allMatch(item -> item.getSourcingStatus() != null
                                    && item.getSourcingStatus().getRecId()
                                    .equals(SOURCING_QUALIFIED_SUPPLIER.id()));
                    canCopyToPr = isRequestCompleted && allItemsQualified;
                }

                approverRequestDto.getApprovalStatus().setCanCopyToPR(canCopyToPr);
            }

            approverRequestDto.setCurrencyObj(
                    CurrencyMapper.INSTANCE.currencyToCurrencyDto(request.getCurrency()));
        }
        return approverRequestDto;
    }

    default ApproverRequestDto toApproverRequestDto(Request request, String timeZone) {
        ApproverRequestDto approverRequestDto = toApproverRequestDto(request);
        if (approverRequestDto != null) {
            approverRequestDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(approverRequestDto.getCreatedDate(), timeZone));
            approverRequestDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(approverRequestDto.getUpdatedDate(), timeZone));
            approverRequestDto.setExpectedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(approverRequestDto.getExpectedDate(), timeZone));
            approverRequestDto.setRequestDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(approverRequestDto.getRequestDate(), timeZone));
            approverRequestDto.setApprovalDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(approverRequestDto.getApprovalDate(), timeZone));
            approverRequestDto.setRequestStatus(
                    RequestStatusMapper.INSTANCE.toRequestStatusDto(request.getRequestStatus(), timeZone));
            approverRequestDto.setApprovalStatus(
                    ApprovalStatusMapper.INSTANCE.toApprovalStatusDto(request.getApprovalStatus(), timeZone));
            approverRequestDto.setCurrencyObj(
                    CurrencyMapper.INSTANCE.currencyToCurrencyDto(request.getCurrency()));
//            approverRequestDto.setBudgetType(
//                    BudgetTypeMapper.INSTANCE.toBudgetTypeOptionDto(request.getBudgetType(), timeZone));
        }
        return approverRequestDto;
    }

    default RequesterRequestDto toRequesterRequestDto(Request request, String timeZone) {
        RequesterRequestDto requesterRequestDto = toRequesterRequestDto(request);
        if (requesterRequestDto != null) {
            requesterRequestDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requesterRequestDto.getCreatedDate(), timeZone));
            requesterRequestDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requesterRequestDto.getUpdatedDate(), timeZone));
/*          Fix PTVNBAY-1797 by Veerapat.pre
            Issue is timezone save as no timezone by get with timezone
            requesterRequestDto.setExpectedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requesterRequestDto.getExpectedDate(), timeZone));
*/
            requesterRequestDto.setRequestDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requesterRequestDto.getRequestDate(), timeZone));
            requesterRequestDto.setApprovalDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requesterRequestDto.getApprovalDate(), timeZone));
            requesterRequestDto.setRequestStatus(
                    RequestStatusMapper.INSTANCE.toRequestStatusDto(request.getRequestStatus(), timeZone));
            requesterRequestDto.setApprovalStatus(
                    ApprovalStatusMapper.INSTANCE.toApprovalStatusDto(request.getApprovalStatus(), timeZone));
            requesterRequestDto.setCurrencyObj(
                    CurrencyMapper.INSTANCE.currencyToCurrencyDto(request.getCurrency()));
            requesterRequestDto.setRequestDeptApproverDtoList(
                    RequestApproverMapper.INSTANCE.toRequestDeptApproverDtoList(request.getRequestDeptApproverList(), timeZone)
            );
            requesterRequestDto.setRequestPurchaserList(
                    RequestPurchaserMapper.INSTANCE.toRequestPurchaserDtoList(request.getRequestPurchaserList(), timeZone)
            );
            requesterRequestDto.setRequestDto(
                    RequestMapper.INSTANCE.toRequestDto(request, timeZone)
            );
//            requesterRequestDto.setBudgetType(
//                    BudgetTypeMapper.INSTANCE.toBudgetTypeDto(request.getBudgetType(), timeZone));
        }
        return requesterRequestDto;
    }

    default RequesterRequestDto toRequesterRequestDto(Request request, String timeZone, List<String> copyToPrPrivilegeCodes,Boolean forceSelectAllItemCopyToPR, List<String> copyToPrOrganizations) {
        RequesterRequestDto requesterRequestDto = toRequesterRequestDto(request);
        if (requesterRequestDto != null) {
            requesterRequestDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requesterRequestDto.getCreatedDate(), timeZone));
            requesterRequestDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requesterRequestDto.getUpdatedDate(), timeZone));
/*          Fix PTVNBAY-1797 by Veerapat.pre
            Issue is timezone save as no timezone by get with timezone
            requesterRequestDto.setExpectedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requesterRequestDto.getExpectedDate(), timeZone));
*/
            requesterRequestDto.setRequestDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requesterRequestDto.getRequestDate(), timeZone));
            requesterRequestDto.setApprovalDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requesterRequestDto.getApprovalDate(), timeZone));
            requesterRequestDto.setRequestStatus(
                    RequestStatusMapper.INSTANCE.toRequestStatusDto(request.getRequestStatus(), timeZone));
            requesterRequestDto.setApprovalStatus(
                    ApprovalStatusMapper.INSTANCE.toApprovalStatusDto(request.getApprovalStatus(), timeZone));
            requesterRequestDto.setCurrencyObj(
                    CurrencyMapper.INSTANCE.currencyToCurrencyDto(request.getCurrency()));
            requesterRequestDto.setRequestDeptApproverDtoList(
                    RequestApproverMapper.INSTANCE.toRequestDeptApproverDtoList(request.getRequestDeptApproverList(), timeZone)
            );
            requesterRequestDto.setRequestPurchaserList(
                    RequestPurchaserMapper.INSTANCE.toRequestPurchaserDtoList(request.getRequestPurchaserList(), timeZone)
            );
            requesterRequestDto.setRequestDto(
                    RequestMapper.INSTANCE.toRequestDto(request, timeZone)
            );
//            requesterRequestDto.setBudgetType(
//                    BudgetTypeMapper.INSTANCE.toBudgetTypeDto(request.getBudgetType(), timeZone));

            boolean canCopyToPr = false;

            if (requesterRequestDto.getApprovalStatus() != null && requesterRequestDto.getApprovalStatus().isCanCopyToPR()) {

                // ตรวจสิทธิ์จาก privilegeCode
                boolean canCopyToPrByPrivilegeCode = AppUtil.checkCanCopyToPRByPrivilegeCode(copyToPrPrivilegeCodes);

                // ตรวจสิทธิ์จาก organizationId
                boolean canCopyToPrByOrganizations = true; // default = true ถ้าไม่มี list ให้ข้ามการเช็ค
                if (copyToPrOrganizations != null && !copyToPrOrganizations.isEmpty()) {
                    canCopyToPrByOrganizations = request.getOrganizationId() != null
                            && copyToPrOrganizations.stream()
                            .anyMatch(org -> org.equalsIgnoreCase(String.valueOf(request.getOrganizationId())));
                }

                canCopyToPr = canCopyToPrByPrivilegeCode && canCopyToPrByOrganizations;

                if(forceSelectAllItemCopyToPR){
                    boolean isRequestCompleted = request.getRequestStatus() != null
                            && TENANT_REQUEST_COMPLETED.code().equalsIgnoreCase(request.getRequestStatus().getName());

                    boolean allItemsQualified = request.getRequestItemList() != null
                            && !request.getRequestItemList().isEmpty()
                            && request.getRequestItemList().stream()
                            .allMatch(item -> item.getSourcingStatus() != null
                                    && item.getSourcingStatus().getRecId()
                                    .equals(SOURCING_QUALIFIED_SUPPLIER.id()));
                    canCopyToPr = isRequestCompleted && allItemsQualified;
                }

                requesterRequestDto.getRequestStatus().setCanCopyToPR(canCopyToPr);
            }
        }
        return requesterRequestDto;
    }

    default ReviewerRequestDto toReviewerRequestDto(Request request, String timeZone) {
        ReviewerRequestDto reviewerRequestDto = toReviewerRequestDto(request);
        if (reviewerRequestDto != null) {
            reviewerRequestDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(reviewerRequestDto.getCreatedDate(), timeZone));
            reviewerRequestDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(reviewerRequestDto.getUpdatedDate(), timeZone));
            reviewerRequestDto.setExpectedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(reviewerRequestDto.getExpectedDate(), timeZone));
            reviewerRequestDto.setRequestDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(reviewerRequestDto.getRequestDate(), timeZone));
            reviewerRequestDto.setApprovalDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(reviewerRequestDto.getApprovalDate(), timeZone));
            reviewerRequestDto.setRequestStatus(
                    RequestStatusMapper.INSTANCE.toRequestStatusDto(request.getRequestStatus(), timeZone));
            reviewerRequestDto.setApprovalStatus(
                    ApprovalStatusMapper.INSTANCE.toApprovalStatusDto(request.getApprovalStatus(), timeZone));
            reviewerRequestDto.setCurrencyObj(
                    CurrencyMapper.INSTANCE.currencyToCurrencyDto(request.getCurrency()));
//            reviewerRequestDto.setBudgetType(
//                    BudgetTypeMapper.INSTANCE.toBudgetTypeDto(request.getBudgetType(), timeZone));
        }
        return reviewerRequestDto;
    }

    default DeptApproverRequestDto toDeptApproverRequestDto(Request request, String timeZone) {
        DeptApproverRequestDto deptApproverRequestDto = toDeptApproverRequestDto(request);
        if (deptApproverRequestDto != null) {
            deptApproverRequestDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(deptApproverRequestDto.getCreatedDate(), timeZone));
            deptApproverRequestDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(deptApproverRequestDto.getUpdatedDate(), timeZone));
            deptApproverRequestDto.setExpectedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(deptApproverRequestDto.getExpectedDate(), timeZone));
            deptApproverRequestDto.setRequestDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(deptApproverRequestDto.getRequestDate(), timeZone));
            deptApproverRequestDto.setApprovalDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(deptApproverRequestDto.getApprovalDate(), timeZone));
            deptApproverRequestDto.setRequestStatus(
                    RequestStatusMapper.INSTANCE.toRequestStatusDto(request.getRequestStatus(), timeZone));
            deptApproverRequestDto.setDeptApprovalStatus(
                    DeptApprovalStatusMapper.INSTANCE.toDeptApprovalStatusDto(request.getDeptApprovalStatus(), timeZone));
            deptApproverRequestDto.setApprovalStatus(
                    ApprovalStatusMapper.INSTANCE.toApprovalStatusDto(request.getApprovalStatus(), timeZone));
            deptApproverRequestDto.setCurrencyObj(
                    CurrencyMapper.INSTANCE.currencyToCurrencyDto(request.getCurrency()));
            deptApproverRequestDto.setRequestDeptApproverDtoList(
                    RequestApproverMapper.INSTANCE.toRequestDeptApproverDtoList(request.getRequestDeptApproverList(), timeZone)
            );
//            reviewerRequestDto.setBudgetType(
//                    BudgetTypeMapper.INSTANCE.toBudgetTypeDto(request.getBudgetType(), timeZone));
        }
        return deptApproverRequestDto;
    }

    default SourcingApproverRequestDto toSourcingApproverRequestDto(Request request, String timeZone) {
        SourcingApproverRequestDto sourcingApproverRequestDto = toSourcingApproverRequestDto(request);
        if (sourcingApproverRequestDto != null) {
            sourcingApproverRequestDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(sourcingApproverRequestDto.getCreatedDate(), timeZone));
            sourcingApproverRequestDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(sourcingApproverRequestDto.getUpdatedDate(), timeZone));
            sourcingApproverRequestDto.setExpectedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(sourcingApproverRequestDto.getExpectedDate(), timeZone));
            sourcingApproverRequestDto.setRequestDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(sourcingApproverRequestDto.getRequestDate(), timeZone));
            sourcingApproverRequestDto.setApprovalDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(sourcingApproverRequestDto.getApprovalDate(), timeZone));
            sourcingApproverRequestDto.setRequestStatus(
                    RequestStatusMapper.INSTANCE.toRequestStatusDto(request.getRequestStatus(), timeZone));
            sourcingApproverRequestDto.setDeptApprovalStatus(
                    DeptApprovalStatusMapper.INSTANCE.toDeptApprovalStatusDto(request.getDeptApprovalStatus(), timeZone));
            sourcingApproverRequestDto.setApprovalStatus(
                    ApprovalStatusMapper.INSTANCE.toApprovalStatusDto(request.getApprovalStatus(), timeZone));
            sourcingApproverRequestDto.setCurrencyObj(
                    CurrencyMapper.INSTANCE.currencyToCurrencyDto(request.getCurrency()));
            sourcingApproverRequestDto.setRequestDeptApproverDtoList(
                    RequestApproverMapper.INSTANCE.toRequestDeptApproverDtoList(request.getRequestDeptApproverList(), timeZone)
            );
//            reviewerRequestDto.setBudgetType(
//                    BudgetTypeMapper.INSTANCE.toBudgetTypeDto(request.getBudgetType(), timeZone));

//            List<RequestItemV2Dto> requestItemList = sourcingApproverRequestDto.getRequestItemList().stream()
//                    .filter(item -> Objects.equals(item.getSourcingTypeId(), SOURCING_TYPE_EXCEPTIONAL_SOURCING.id()))
//                    .collect(Collectors.toList());
//            sourcingApproverRequestDto.setRequestItemList(requestItemList);
        }
        return sourcingApproverRequestDto;
    }

    default ReportLineDto toReportLineDto(ReportLine reportLine, String timeZone) {
        ReportLineDto reportLineResultDto = toReportLineDto(reportLine);
        if (reportLineResultDto != null) {
            reportLineResultDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(reportLineResultDto.getCreatedDate(), timeZone));
        }
        return reportLineResultDto;
    }

    default RequesterDto toRequesterDto(Requester requester, String timeZone) {
        RequesterDto requesterResultDto = toRequesterDto(requester);
        if (requesterResultDto != null) {
            requesterResultDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(requesterResultDto.getCreatedDate(), timeZone));
        }
        return requesterResultDto;
    }

    default ReviewerDto toReviewerDto(Reviewer reviewer, String timeZone) {
        ReviewerDto reviewerResultDto = toReviewerDto(reviewer);
        if (reviewerResultDto != null) {
            reviewerResultDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(reviewerResultDto.getCreatedDate(), timeZone));
        }
        return reviewerResultDto;
    }

    default ApproverDto toApproverDto(Approver approver, String timeZone) {
        ApproverDto approverDto = toApproverDto(approver);
        if (approverDto != null) {
            approverDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(approverDto.getCreatedDate(), timeZone));
        }
        return approverDto;
    }

    default PurchaserDto toPurchaserDto(Purchaser purchaser, String timeZone) {
        PurchaserDto purchaserDto = toPurchaserDto(purchaser);
        if (purchaserDto != null) {
            purchaserDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(purchaserDto.getCreatedDate(), timeZone));
            purchaserDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(purchaserDto.getUpdatedDate(), timeZone));

        }
        return purchaserDto;
    }

    default ProjectDto toProjectDto(Project project, String timeZone) {
        ProjectDto projectDto = toProjectDto(project);
        if (projectDto != null) {
            projectDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(projectDto.getCreatedDate(), timeZone));
            projectDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(projectDto.getUpdatedDate(), timeZone));
        }
        return projectDto;
    }

    default DepartmentDto toDepartmentDto(Department department, String timeZone) {
        DepartmentDto departmentDto = toDepartmentDto(department);
        if (departmentDto != null) {
            departmentDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(departmentDto.getCreatedDate(), timeZone));
            departmentDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(departmentDto.getUpdatedDate(), timeZone));
        }
        return departmentDto;
    }

    default ProjectChangeLogHeaderDto toProjectChangeLogHeaderDto(ProjectChangeLogHeader projectChangeLogHeader, String timeZone) {
        ProjectChangeLogHeaderDto projectChangeLogHeaderDto = toProjectChangeLogHeaderDto(projectChangeLogHeader);
        if (projectChangeLogHeaderDto != null) {
            projectChangeLogHeaderDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(projectChangeLogHeaderDto.getCreatedDate(), timeZone));
        }
        return projectChangeLogHeaderDto;
    }

    default CategoryPurchaserDto toCategoryPurchaserDto(CategoryPurchaser categoryPurchaser, String timeZone) {
        return toCategoryPurchaserDto(categoryPurchaser);
    }

    default List<ApproverRequestDto> toApproverRequestDtoList(List<Request> requestList, String timeZone) {
        List<ApproverRequestDto> approverRequestDtoList = new ArrayList();
        if (requestList != null) {
            requestList.forEach(i -> approverRequestDtoList.add(toApproverRequestDto(i, timeZone)));
        }
        return approverRequestDtoList;
    }

    default List<ApproverRequestDto> toApproverRequestDtoList(List<Request> requestList, String timeZone, List<String> copyToPrPrivilegeCodes, Boolean forceSelectAllItemCopyToPR, List<String> copyToPrOrganizations) {
        List<ApproverRequestDto> approverRequestDtoList = new ArrayList();
        if (requestList != null) {
            requestList.forEach(i -> approverRequestDtoList.add(toApproverRequestDto(i, timeZone, copyToPrPrivilegeCodes, forceSelectAllItemCopyToPR, copyToPrOrganizations)));
        }
        return approverRequestDtoList;
    }

    default List<RequesterRequestDto> toRequesterRequestDtoList(List<Request> requestList, String timeZone) {
        List<RequesterRequestDto> requesterRequestDtoList = new ArrayList();
        if(requestList != null) {
            requestList.forEach(i -> requesterRequestDtoList.add(toRequesterRequestDto(i, timeZone)));
        }
        return requesterRequestDtoList;
    }

    default List<RequesterRequestDto> toRequesterRequestDtoList(List<Request> requestList, String timeZone, List<String> copyToPrPrivilegeCodes, Boolean forceSelectAllItemCopyToPR, List<String> copyToPrOrganizations) {
        List<RequesterRequestDto> requesterRequestDtoList = new ArrayList();
        if(requestList != null) {
            requestList.forEach(i -> requesterRequestDtoList.add(toRequesterRequestDto(i, timeZone, copyToPrPrivilegeCodes, forceSelectAllItemCopyToPR, copyToPrOrganizations)));
        }
        return requesterRequestDtoList;
    }

    default List<ReviewerRequestDto> toReviewerRequestDtoList(List<Request> requestList, String timeZone) {
        List<ReviewerRequestDto> reviewerRequestDtoList = new ArrayList();
        if (requestList != null) {
            requestList.forEach(i -> reviewerRequestDtoList.add(toReviewerRequestDto(i, timeZone)));
        }
        return reviewerRequestDtoList;
    }

    default List<DeptApproverRequestDto> toDeptApproverRequestDtoList(List<Request> requestList, String timeZone) {
        List<DeptApproverRequestDto> deptApproverRequestDtoList = new ArrayList();
        if (requestList != null) {
            requestList.forEach(i -> deptApproverRequestDtoList.add(toDeptApproverRequestDto(i, timeZone)));
        }
        return deptApproverRequestDtoList;
    }

    default List<SourcingApproverRequestDto> toSourcingApproverRequestDtoList(List<Request> requestList, String timeZone) {
        List<SourcingApproverRequestDto> sourcingApproverRequestDtoList = new ArrayList();
        if (requestList != null) {
            requestList.forEach(i -> sourcingApproverRequestDtoList.add(toSourcingApproverRequestDto(i, timeZone)));
        }
        return sourcingApproverRequestDtoList;
    }

    default List<ReportLineDto> toReportLineListDto(List<ReportLine> reportLines, String timeZone) {
        List<ReportLineDto> reportLineDtoList = new ArrayList<>();
        if (reportLines != null) {
            reportLines.forEach(i -> reportLineDtoList.add(toReportLineDto(i, timeZone)));
        }
        return reportLineDtoList;
    }

    default List<RequesterDto> toRequesterListDto(List<Requester> requesters, String timeZone) {
        List<RequesterDto> requesterDtoList = new ArrayList<>();
        if (requesters != null) {
            requesters.forEach(i -> requesterDtoList.add(toRequesterDto(i, timeZone)));
        }
        return requesterDtoList;
    }

    default List<ReviewerDto> toReviewerListDto(List<Reviewer> reviewers, String timeZone) {
        List<ReviewerDto> reviewerDtoList = new ArrayList<>();
        if (reviewers != null) {
            reviewers.forEach(i -> reviewerDtoList.add(toReviewerDto(i, timeZone)));
        }
        return reviewerDtoList;
    }

    default List<ApproverDto> toApproverListDto(List<Approver> content, String timeZone) {
        List<ApproverDto> approverDtoList = new ArrayList<>();
        if (content != null) {
            content.forEach(i -> approverDtoList.add(toApproverDto(i, timeZone)));
        }
        return approverDtoList;
    }

    default List<PurchaserDto> toPurchaserDtoList(List<Purchaser> purchasers, String timeZone) {
        List<PurchaserDto> purchaserDtoList = new ArrayList<>();
        if (purchasers != null) {
            purchasers.forEach(i -> purchaserDtoList.add(toPurchaserDto(i, timeZone)));
        }
        return purchaserDtoList;
    }

    default List<ProjectDto> toProjectDtoList(List<Project> projects, String timeZone) {
        List<ProjectDto> projectDtoList = new ArrayList<>();
        if (projects != null) {
            projects.forEach(i -> projectDtoList.add(toProjectDto(i, timeZone)));
        }
        return projectDtoList;
    }

    default ProjectDto toProjectSingleDto(Project project, String timeZone) {
        return toProjectDto(project, timeZone);
    }

    default List<DepartmentDto> toDepartmentDtoList(List<Department> departments, String timeZone) {
        List<DepartmentDto> departmentList = new ArrayList<>();
        if (departments != null) {
            departments.forEach(i -> departmentList.add(toDepartmentDto(i, timeZone)));
        }
        return departmentList;
    }

    default List<ProjectChangeLogHeaderDto> toProjectChangeLogHeaderDtoList(List<ProjectChangeLogHeader> projectChangeLogHeaders, String timeZone) {
        List<ProjectChangeLogHeaderDto> projectChangeLogHeaderDtoList = new ArrayList<>();
        if (projectChangeLogHeaders != null) {
            projectChangeLogHeaders.forEach(i -> projectChangeLogHeaderDtoList.add(toProjectChangeLogHeaderDto(i, timeZone)));
        }
        return projectChangeLogHeaderDtoList;
    }

}

