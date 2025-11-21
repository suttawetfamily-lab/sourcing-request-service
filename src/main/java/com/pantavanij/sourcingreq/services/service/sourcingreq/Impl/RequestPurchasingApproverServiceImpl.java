package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.EpAuthClient;
import com.pantavanij.sourcingreq.services.domain.dto.EPAuthDeptApproverDto;
import com.pantavanij.sourcingreq.services.domain.dto.EPAuthUserDTO;
import com.pantavanij.sourcingreq.services.domain.request.DeptApproverSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.EPAuthUserSearchRequest;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthDeptApproverResponse;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthUserListResponse;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.ApproverRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.DeptApprovalStatusRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestApproverRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.pantavanij.sourcingreq.services.enums.Role.EXC_PURCHASING_APPROVER;

@RequiredArgsConstructor
@Service
@Slf4j
public class RequestPurchasingApproverServiceImpl implements RequestPurchasingApproverService {
    private final RequestApproverRepository requestApproverRepository;
    private final RequestRepository requestRepository;
    private final UaaService uaaService;
    //private final TenantConfigService tenantConfigService;
    private final TenantService tenantService;
    private final EpAuthClient epAuthClient;
    private final ApproverRepository approverRepository;
    private final DeptApprovalStatusRepository deptApprovalStatusRepository;

//    @Override
//    public List<RequestDeptApproverDto> findByRequest(Long requestId) {
//        List<RequestDeptApproverDto> requestDeptApproverDtos = new ArrayList<>();
//        Request request = requestRepository.findRequestsByRecId(requestId);
//        List<RequestApprover> requestApprovers = requestApproverRepository.findRequestApproversByRequest(request);
//        if(requestApprovers != null && !requestApprovers.isEmpty()) {
//            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
//            requestDeptApproverDtos = RequestApproverMapper.INSTANCE.toRequestDeptApproverDtoList(requestApprovers, timeZone);
//            requestDeptApproverDtos.stream()
//                    .peek(requestDeptApproverDto -> {
//                        requestDeptApproverDto.setCreatedByName(UserDetailServiceUtil.getFullName(requestDeptApproverDto.getCreatedBy()));
//                    }).collect(Collectors.toList());
//        }
//        return requestDeptApproverDtos;
//    }
//
//    @Override
//    public RequestDeptApproverDto findCurrentAwaitingDeptApprover(Long requestId) {
//        RequestDeptApproverDto requestDeptApproverDto = null;
//        Optional<RequestApprover> requestApprover = requestApproverRepository.findCurrentAwaitingDeptApprover(requestId);
//        if(requestApprover.isPresent() ) {
//            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
//            requestDeptApproverDto = RequestApproverMapper.INSTANCE.toRequestDeptApproverDto(requestApprover.get(), timeZone);
//            requestDeptApproverDto.setCreatedByName(UserDetailServiceUtil.getFullName(requestDeptApproverDto.getCreatedBy()));
//        }
//        return requestDeptApproverDto;
//    }
//
//    @Override
//    public RequestDeptApproverDto findLatestApprovedDeptApprover(Long requestId) {
//        RequestDeptApproverDto requestDeptApproverDto = null;
//        Optional<RequestApprover> requestApprover = requestApproverRepository.findLatestApprovedDeptApprover(requestId);
//        if(requestApprover.isPresent() ) {
//            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
//            requestDeptApproverDto = RequestApproverMapper.INSTANCE.toRequestDeptApproverDto(requestApprover.get(), timeZone);
//            requestDeptApproverDto.setCreatedByName(UserDetailServiceUtil.getFullName(requestDeptApproverDto.getCreatedBy()));
//        }
//        return requestDeptApproverDto;
//    }
//
//    @Override
//    public RequestDeptApproverDto find1StCancelledDeptApprover(Long requestId) {
//        RequestDeptApproverDto requestDeptApproverDto = null;
//        Optional<RequestApprover> requestApprover = requestApproverRepository.find1StCancelledDeptApprover(requestId);
//        if(requestApprover.isPresent() ) {
//            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
//            requestDeptApproverDto = RequestApproverMapper.INSTANCE.toRequestDeptApproverDto(requestApprover.get(), timeZone);
//            requestDeptApproverDto.setCreatedByName(UserDetailServiceUtil.getFullName(requestDeptApproverDto.getCreatedBy()));
//        }
//        return requestDeptApproverDto;
//    }
//
//    @Override
//    public List<RequestDeptApproverDto> findAllApprovedDeptApprover(Long requestId) {
//        List<RequestDeptApproverDto> requestDeptApproverDtos = new ArrayList<>();
//        Request request = requestRepository.findRequestsByRecId(requestId);
//        DeptApprovalStatus deptApprovalStatusNoneObj = deptApprovalStatusRepository.findByName(DEPT_APPROVAL_APPROVED.code());
//        List<RequestApprover> requestApprovers = requestApproverRepository.findRequestApproversByRequestAndDeptApprovalStatus(request, deptApprovalStatusNoneObj);
//        if(requestApprovers != null && !requestApprovers.isEmpty()) {
//            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
//            requestDeptApproverDtos = RequestApproverMapper.INSTANCE.toRequestDeptApproverDtoList(requestApprovers, timeZone);
//            requestDeptApproverDtos.stream()
//                    .peek(requestDeptApproverDto -> {
//                        requestDeptApproverDto.setCreatedByName(UserDetailServiceUtil.getFullName(requestDeptApproverDto.getCreatedBy()));
//                    }).collect(Collectors.toList());
//        }
//        return requestDeptApproverDtos;
//    }
//
//    @Override
//    public List<Long> findAllRelatedRequestIds() {
//        List<Long> requestIds = new ArrayList<>();
//
//
//        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
//        Optional<Approver> approver = approverRepository.findApproverByTenantAndLoginId(tenant, AppUtil.getUserName());
//        if(approver.isPresent()) {
//            List<RequestApprover> requestApproverList = requestApproverRepository.findRequestApproverByApprover(approver.get());
//
//            if (requestApproverList != null && !requestApproverList.isEmpty()) {
//                for (RequestApprover requestApprover : requestApproverList) {
//                    requestIds.add(requestApprover.getRequest().getRecId());
//                }
//            }
//        }
//
//        return requestIds;
//    }

    @Override
    public EPAuthDeptApproverResponse getPurchasingApproverListByConditions(DeptApproverSearchRequest request) {
        EPAuthUserSearchRequest epAuthUserSearchRequest = EPAuthUserSearchRequest.builder()
                .conditionSearchList(request.getConditionSearchList())
                .tenantId(request.getTenantId())
                .page(request.getPage())
                .pageSize(request.getPageSize())
                .sortBy(request.getSortBy())
                .sortOrder(request.getSortOrder())
                .build();

//        String tenantCode = AppUtil.getTenantId();
//        Tenant tenant = tenantService.findByCode(tenantCode);
        String[] privilegeCode = new String[] {EXC_PURCHASING_APPROVER.privilegeCode()};
//        String privilegeCode = tenantConfigService.getEPAuthPrivilegeCode(tenant.getRecId(), Role.DEPT_APPROVER.roleName());
        EPAuthUserListResponse epAuthUserListResponse = new EPAuthUserListResponse();
        try {
            epAuthUserListResponse = epAuthClient.getEPAuthUserList(privilegeCode, epAuthUserSearchRequest);
        } catch(Exception ex) {
            ex.getMessage();
        }
        List<EPAuthDeptApproverDto> epAuthDeptApproverDtoList = new ArrayList<>();

        if (epAuthUserListResponse.getData() == null) {
            return EPAuthDeptApproverResponse.builder()
                    .data(null)
                    .page(epAuthUserListResponse.getPage())
                    .pageSize(epAuthUserListResponse.getPageSize())
                    .total(epAuthUserListResponse.getTotal())
                    .totalPage(epAuthUserListResponse.getTotalPage())
                    .build();
        }

        List<EPAuthUserDTO> epAuthUserList = epAuthUserListResponse.getData();


        // Sort data by fullname
        epAuthUserList.sort(Comparator.comparing(EPAuthUserDTO::getFullName));

        for (EPAuthUserDTO epAuthUserDTO: epAuthUserList) {
            epAuthDeptApproverDtoList.add(
                    EPAuthDeptApproverDto.builder()
                            .sysUserId(epAuthUserDTO.getSysUserId())
                            .loginId(epAuthUserDTO.getLoginId())
                            .fullName(epAuthUserDTO.getFullName())
                            .email(epAuthUserDTO.getEmail())
                            .mobilePhone(epAuthUserDTO.getMobilePhone())
                            .phone(epAuthUserDTO.getPhone())
                            .timezone(epAuthUserDTO.getTimezone())
                            .build());
        }
        return EPAuthDeptApproverResponse.builder()
                .data(epAuthDeptApproverDtoList)
                .page(epAuthUserListResponse.getPage())
                .pageSize(epAuthUserListResponse.getPageSize())
                .total(epAuthUserListResponse.getTotal())
                .totalPage(epAuthUserListResponse.getTotalPage())
                .build();
    }



//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void deleteByRequestId(Long requestId) {
//        log.info("Delete all Dept Approver by Request ID : {}", requestId);
//        requestApproverRepository.deleteRequestApproverByRequestId(requestId);
//    }
//
//    @Override
//    public boolean hasApprovalPermission(RequestApprover requestApprover) {
//        boolean result = false;
//        DeptApprovalStatus deptApproverStatus = requestApprover.getDeptApprovalStatus();
//        if(deptApproverStatus.getName().equalsIgnoreCase(DEPT_APPROVAL_AWAITING.code())) {
//            result = true;
//        }
//        return result;
//    }
//
//    @Override
//    public boolean isLastDeptApprover(Long requestId) {
//        boolean result = false;
//        Request request = requestRepository.findRequestsByRecId(requestId);
//
//        DeptApprovalStatus deptApprovalStatusAwaitingObj = deptApprovalStatusRepository.findByName(DEPT_APPROVAL_AWAITING.code());
//        List<RequestApprover> requestApprovers = requestApproverRepository.findRequestApproversByRequestAndDeptApprovalStatus(request, deptApprovalStatusAwaitingObj);
//
//        if(requestApprovers == null || requestApprovers.isEmpty()) {
//            result = true;
//        }
//        return result;
//    }


}
