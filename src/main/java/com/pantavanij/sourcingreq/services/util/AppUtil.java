package com.pantavanij.sourcingreq.services.util;

import com.pantavanij.sourcingreq.services.domain.dto.DelegationDto;
import com.pantavanij.sourcingreq.services.domain.dto.UserDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.enums.Role;
import com.pantavanij.sourcingreq.services.enums.Activity;
import lombok.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

import static com.pantavanij.sourcingreq.services.enums.TenantRequestStatus.*;

@RequiredArgsConstructor
public class AppUtil {

    public static String getJwtToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            Object jwtToken = attributes.getAttribute("jwtToken", 0);
            assert jwtToken != null;
            return jwtToken.toString();
        } else {
            return null;
        }
    }

    public static String getTenantId() {
        UserDto userDto = getUser();
        if (userDto != null) {
            return userDto.getTenantId();
        } else {
            return null;
        }
    }

    public static String getPrivilegeScope(String privilegeCode) {
        UserDto userDto = getUser();
        if (userDto != null) {
            String privilegeScope = userDto.getPrivilegeScope().get(privilegeCode.toUpperCase());
            return null == privilegeScope ? "" : privilegeScope;
        } else {
            return "";
        }
    }

    public static Map<String, String>  getPrivilegeScopes() {
        UserDto userDto = getUser();
        if (userDto != null) {
            return userDto.getPrivilegeScope();
        } else {
            return Collections.emptyMap();
        }
    }

    public static String getUserName() {
        UserDto userDto = getUser();
        if (userDto != null) {
            return userDto.getUsername();
        } else {
            return null;
        }
    }

    public static UserDto getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDto) {
            return (UserDto) authentication.getPrincipal();
        }
        return null;
    }

    public static String getIdp() {
        UserDto userDTO = getUser();
        if (userDTO != null) {
            return userDTO.getIdp();
        }
        return null;
    }

    public static boolean isRequester() {
        return StringUtils.isNotEmpty(getPrivilegeScope(Role.REQUESTER.privilegeCode()));
    }

    public static boolean isPurchaser() {
        return StringUtils.isNotEmpty(getPrivilegeScope(Role.PURCHASER.privilegeCode()));
    }

    public static boolean isDeptApprover() {
        return StringUtils.isNotEmpty(getPrivilegeScope(Role.DEPT_APPROVER.privilegeCode()));
    }

    public static boolean isReviewer() {
        return StringUtils.isNotEmpty(getPrivilegeScope(Role.REVIEWER.privilegeCode()));
    }

    public static boolean isExcSourcingDeptApprover() {
        return StringUtils.isNotEmpty(getPrivilegeScope(Role.EXC_DEPT_APPROVER.privilegeCode()));
    }

    public static boolean isExcSourcingPurchasingApprover() {
        return StringUtils.isNotEmpty(getPrivilegeScope(Role.EXC_PURCHASING_APPROVER.privilegeCode()));
    }

    public static boolean isReportLine() {
        return StringUtils.isNotEmpty(getPrivilegeScope(Role.REPORT_LINE.privilegeCode()));
    }

    public static boolean checkCanCopyToPRByPrivilegeCode(List<String> canCopyToPRprivilegeCodes) {
        return canCopyToPRprivilegeCodes != null
                && !canCopyToPRprivilegeCodes.isEmpty()
                && canCopyToPRprivilegeCodes.stream().anyMatch(code -> StringUtils.isNotEmpty(getPrivilegeScope(code)));
    }

    public static boolean isAllowedAction(UserDto user, Request request, List<RequestPurchaser> requestPurchasers, List<RequestApprover> requestApprovers, List<ExcSourcingApprover> excSourcingApprovers, List<ExcSourcingPurchaser> excSourcingPurchasers, List<RequestReviewer> requestReviewers, List<RequestReportLine> reportLines, DelegationDto delegationDto, List<TenantRequestStatus> tenantRequestStatuses, Activity activity) {
        boolean result = false;
        if (user == null || request == null ) {
            return false;
        }

        if (activity.id() > 0) {
            switch(activity) {
                // Requester -> Duplicate(3)
                // Draft, Awaiting, Completed, Partial complete, Reject -> Can duplicate.
                // Cancel -> Can not duplicate.
                // Enum -> Activity.class
                case ACTIVITY_DUPLICATE:
                    if (!request.getRequestStatus().getName().equals(TENANT_REQUEST_CANCELLED.code())) {
                        result = isDuplicatePermission(user, request, delegationDto, tenantRequestStatuses);
                    }
                    break;

                // Requester -> Cancel(4)
                // Enum -> Activity.class
                case ACTIVITY_CANCEL:
                    if (request.getRequestStatus().getName().equals(TENANT_REQUEST_AWAITING.code()) ||
                            request.getRequestStatus().getName().equals(TENANT_REQUEST_PENDING.code())) {
                        result = isCancelPermission(user, request, delegationDto, tenantRequestStatuses);
                    }
                    break;

                // Approver -> Confirm(5), Reject(6), Convert to eRFX(16)
                // Enum -> Activity.class
                case ACTIVITY_CONFIRM:
                case ACTIVITY_REJECT:
                case ACTIVITY_CONVERT_ERFX:
                    if (AppUtil.isPurchaser()) { // && request.getApprovalStatus().getName().equals(TENANT_APPROVAL_AWAITING.code())
                        result = isApproverPermission(user, request, requestPurchasers, delegationDto);
                    }
                    break;

                // Approver -> Remove(8)
                // Enum -> Activity.class
                case ACTIVITY_REMOVE:
                    if (AppUtil.isPurchaser()) { // && request.getApprovalStatus().getName().equals(TENANT_APPROVAL_AWAITING.code())
                        result = isRemovePermission(user, request, delegationDto);
                    }
                    break;

                // Approver -> Assign(7)
                // Enum -> Activity.class
                case ACTIVITY_ASSIGN:
                    if (AppUtil.isPurchaser()) { // && request.getApprovalStatus().getName().equals(TENANT_APPROVAL_AWAITING.code())) {
                        result = isAssignPermission(user, request, requestPurchasers, delegationDto);
                    }
                    break;

                // Requester -> Edit(13).
                // Approver -> Edit(13)
                // Enum -> Activity.class
                case ACTIVITY_QUESTIONNAIRE:
                case ACTIVITY_EDIT:
                    if (AppUtil.isRequester()) { //  && request.getRequestStatus().getName().equals(TENANT_REQUEST_DRAFT.code())
                        result = isRequesterPermission(user, request, delegationDto);
                    } else if (AppUtil.isPurchaser()) { // && request.getApprovalStatus().getName().equals(TENANT_APPROVAL_AWAITING.code())
                        result = isApproverPermission(user, request, requestPurchasers, delegationDto);
                    }
                    break;

                // Requester -> Delete(15)
                // Enum -> Activity.class
                case ACTIVITY_DELETE:
                    if (request.getRequestStatus().getName().equals(TENANT_REQUEST_DRAFT.code())) {
                        result = isDeletePermission(user, request, delegationDto, tenantRequestStatuses);
                    }
                    break;
            }
        } else {
            result = isFetchPermission(user, request, requestPurchasers, requestApprovers, excSourcingApprovers, excSourcingPurchasers, requestReviewers, reportLines, delegationDto);
        }
        return result;
    }

    public static boolean checkValidReviewer(UserDto user, List<RequestReviewer> requestReviewers) {
        return requestReviewers != null &&
                !requestReviewers.isEmpty() &&
                requestReviewers.stream().anyMatch(r -> r.getReviewer().getLoginId().equalsIgnoreCase(user.getUsername()));
    }

    public static boolean checkValidReportLine(UserDto user, List<RequestReportLine> reportLines) {
        return reportLines != null &&
                !reportLines.isEmpty() &&
                reportLines.stream().anyMatch(r -> r.getReportLine().getLoginId().equalsIgnoreCase(user.getUsername()));
    }

    public static boolean checkValidApprover(UserDto user, List<RequestApprover> requestApprovers) {
        return requestApprovers != null &&
                !requestApprovers.isEmpty() &&
                requestApprovers.stream().anyMatch(r -> r.getApprover().getLoginId().equalsIgnoreCase(user.getUsername()));
    }

    public static boolean checkValidExcSourcingApprover(UserDto user, List<ExcSourcingApprover> excSourcingApprovers) {
        return excSourcingApprovers != null &&
                !excSourcingApprovers.isEmpty() &&
                excSourcingApprovers.stream().anyMatch(r -> r.getApprover().getLoginId().equalsIgnoreCase(user.getUsername()));
    }

    public static boolean checkValidExcSourcingPurchaser(UserDto user, List<ExcSourcingPurchaser> excSourcingPurchasers) {
        return excSourcingPurchasers != null &&
                !excSourcingPurchasers.isEmpty() &&
                excSourcingPurchasers.stream().anyMatch(r -> r.getApprover().getLoginId().equalsIgnoreCase(user.getUsername()));
    }

    public static boolean checkValidPurchaser(UserDto user, List<RequestPurchaser> requestPurchasers) {
        return requestPurchasers != null &&
                !requestPurchasers.isEmpty() &&
                requestPurchasers.stream().anyMatch(r -> r.getPurchaser().getLoginId().equalsIgnoreCase(user.getUsername()));
    }

    public static boolean isFetchPermission(UserDto user, Request request, List<RequestPurchaser> requestPurchasers, List<RequestApprover> requestApprovers, List<ExcSourcingApprover> excSourcingApprovers, List<ExcSourcingPurchaser> excSourcingPurchasers, List<RequestReviewer> requestReviewers, List<RequestReportLine> reportLines, DelegationDto delegationDto) {
        boolean result = false;
        boolean isValidPurchaser = checkValidPurchaser(user, requestPurchasers);
        boolean isValidApprover = checkValidApprover(user, requestApprovers);
        boolean isValidExcSourcingApprover = checkValidExcSourcingApprover(user, excSourcingApprovers);
        boolean isValidExcSourcingPurchaser = checkValidExcSourcingPurchaser(user, excSourcingPurchasers);
        boolean isValidReviewer = checkValidReviewer(user, requestReviewers);
        boolean isValidReportLine = checkValidReportLine(user, reportLines);
        if ((user.getUsername().equalsIgnoreCase(request.getAssignedBy()) ||
                ((delegationDto != null && user.getUsername().equalsIgnoreCase(delegationDto.getDelegateeBy())) || user.getUsername().equalsIgnoreCase(request.getDelegateActionBy())) ||
                user.getUsername().equalsIgnoreCase(request.getCreatedBy()) ||
                isValidPurchaser || isValidApprover || isValidExcSourcingApprover || isValidExcSourcingPurchaser || isValidReviewer || isValidReportLine)) {
            result = true;
        }
        return result;
    }

    public static boolean isDeletePermission(UserDto user, Request request, DelegationDto delegationDto, List<TenantRequestStatus> tenantRequestStatuses) {
        boolean result = false;
        TenantRequestStatus tenantRequestStatus = getTenantRequestStatusFilter(request, tenantRequestStatuses);

        if ((user.getUsername().equalsIgnoreCase(request.getAssignedBy()) ||
                ((delegationDto != null && user.getUsername().equalsIgnoreCase(delegationDto.getDelegateeBy())) || user.getUsername().equalsIgnoreCase(request.getDelegateActionBy())) ||
                user.getUsername().equalsIgnoreCase(request.getCreatedBy())) &&
                (tenantRequestStatus.isCanDelete())) {
            result = true;
        }
        return result;
    }

    public static boolean isDuplicatePermission(UserDto user, Request request, DelegationDto delegationDto, List<TenantRequestStatus> tenantRequestStatuses) {
        boolean result = false;
        TenantRequestStatus tenantRequestStatus = getTenantRequestStatusFilter(request, tenantRequestStatuses);

        if ((user.getUsername().equalsIgnoreCase(request.getAssignedBy()) ||
                ((delegationDto != null && user.getUsername().equalsIgnoreCase(delegationDto.getDelegateeBy())) || user.getUsername().equalsIgnoreCase(request.getDelegateActionBy())) ||
                user.getUsername().equalsIgnoreCase(request.getCreatedBy())) &&
                (tenantRequestStatus.isCanDuplicate())) {
            result = true;
        }
        return result;
    }

    public static boolean isRequesterPermission(UserDto user, Request request, DelegationDto delegationDto) {
        boolean result = false;
        if ((user.getUsername().equalsIgnoreCase(request.getAssignedBy()) ||
                ((delegationDto != null && user.getUsername().equalsIgnoreCase(delegationDto.getDelegateeBy())) || user.getUsername().equalsIgnoreCase(request.getDelegateActionBy())) ||
                user.getUsername().equalsIgnoreCase(request.getCreatedBy()))) {
            result = true;
        }
        return result;
    }

    public static boolean isCancelPermission(UserDto user, Request request, DelegationDto delegationDto, List<TenantRequestStatus> tenantRequestStatuses) {
        boolean result = false;
        TenantRequestStatus tenantRequestStatus = getTenantRequestStatusFilter(request, tenantRequestStatuses);

        if ((user.getUsername().equalsIgnoreCase(request.getAssignedBy()) ||
                ((delegationDto != null && user.getUsername().equalsIgnoreCase(delegationDto.getDelegateeBy())) || user.getUsername().equalsIgnoreCase(request.getDelegateActionBy())) ||
                user.getUsername().equalsIgnoreCase(request.getCreatedBy())) &&
                tenantRequestStatus.isCanCancel()) {
            result = true;
        }
        return result;
    }

    public static boolean isApproverPermission(UserDto user, Request request, List<RequestPurchaser> requestPurchasers, DelegationDto delegationDto) {
        boolean result = false;
        boolean isValidApprover = checkValidPurchaser(user, requestPurchasers);
        if ((user.getUsername().equalsIgnoreCase(request.getAssignedBy()) ||
                ((delegationDto != null && user.getUsername().equalsIgnoreCase(delegationDto.getDelegateeBy())) || user.getUsername().equalsIgnoreCase(request.getDelegateActionBy())) ||
                isValidApprover)) {
            result = true;
        }
        return result;
    }

    public static boolean isRemovePermission(UserDto user, Request request, DelegationDto delegationDto) {
        boolean result = false;
        if (user.getUsername().equalsIgnoreCase(request.getAssignedBy()) ||
                ((delegationDto != null && user.getUsername().equalsIgnoreCase(delegationDto.getDelegateeBy())) || user.getUsername().equalsIgnoreCase(request.getDelegateActionBy()))) {
            result = true;
        }
        return result;
    }

    public static boolean isAssignPermission(UserDto user, Request request, List<RequestPurchaser> requestPurchasers, DelegationDto delegationDto) {
        boolean result = false;
        boolean isValidApprover = checkValidPurchaser(user, requestPurchasers);
        if ((((delegationDto != null && user.getUsername().equalsIgnoreCase(delegationDto.getDelegateeBy())) || user.getUsername().equalsIgnoreCase(request.getDelegateActionBy()))
                || isValidApprover) && request.getAssignedBy() == null) {
            result = true;
        }
        return result;
    }

    public static TenantRequestStatus getTenantRequestStatusFilter(Request request, List<TenantRequestStatus> tenantRequestStatuses) {
        Optional<TenantRequestStatus> tenantRequestStatusesOptional = tenantRequestStatuses.stream()
                .filter(tenantRequestStatus -> tenantRequestStatus.getRequestStatus().getRecId() == request.getStatusId()).findFirst();
        return tenantRequestStatusesOptional.orElse(null);
    }

    public static Map<String, List<String>> convertToMap(String whiteList) {
        if (whiteList.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, List<String>> resultMap = new HashMap<>();
        String[] entries = whiteList.split(",");

        for (String entry : entries) {
            String[] keyValue = entry.split(":", 2);
            String key = keyValue[0];
            String ips = keyValue[1];

            List<String> ipList = Arrays.asList(ips.split("\\|"));
            resultMap.put(key, ipList);
        }
        return resultMap;
    }
}
