package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.EpAuthClient;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestPurchaserKey;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestSubCategoryKey;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.*;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.PurchaserRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestPurchaserRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.Role.*;
import static com.pantavanij.sourcingreq.services.enums.TenantRequestStatus.TENANT_REQUEST_DRAFT;
import static com.pantavanij.sourcingreq.services.enums.TenantRequestStatus.TENANT_REQUEST_PENDING;

@RequiredArgsConstructor
@Service
public class RequestPurchaserServiceImpl implements RequestPurchaserService {

    private final WorkflowInstanceApprovalService workflowInstanceApprovalService;

    private final RequestPurchaserRepository requestPurchaserRepository;

    private final RequestRepository requestRepository;

    private final PurchaserRepository purchaserRepository;

    private final TenantService tenantService;

    private final UaaService uaaService;

    private final TenantConfigService tenantConfigService;

    private final EpAuthClient epAuthClient;

    @Override
    public boolean initializeRequestApprover() {

        String idp = AppUtil.getIdp();
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);

        List<Request> requestList = requestRepository.findRequestsByTenant(tenant);
        List<RequestPurchaser> requestPurchaserAll = new ArrayList<>();

        for(Request request : requestList) {
            if(request.getWorkflowInstanceId() != 0) {

                List<RequestPurchaser> requestPurchaserList = requestPurchaserRepository.findByRequest(request.getRecId());

                if(requestPurchaserList != null && requestPurchaserList.size() == 0) {

                    System.out.println(request.getRecId());
                    // Snapshot all Workflow Approver related to this Request
                    List<InstanceApproverDto> approvers = this.getApprovers(tenantCode, idp, request);
                    List<RequestPurchaser> requestPurchasers = approvers.stream()
                            .filter(item -> item != null)
                            .map(item -> {

                                Purchaser purchaser = new Purchaser();
                                purchaser.setTenant(tenant);
                                purchaser.setPurchaserName(item.getLoginId());
                                purchaser.setCreatedBy(AppUtil.getUserName());
                                purchaser.setCreatedDate(DateTimeUtil.getTimestampUTC());

                                RequestPurchaser requestPurchaser = new RequestPurchaser();
                                requestPurchaser.setRequest(request);
                                requestPurchaser.setPurchaser(purchaser);

                                return requestPurchaser;
                            })
                            .collect(Collectors.toList());
                    requestPurchaserAll.addAll(requestPurchasers);
                }
            }
        }

        requestPurchaserRepository.saveAll(requestPurchaserAll);

        return true;
    }

    @Override
    public SourcingRequestVisibleConfig getApprovalAllTapConfig() {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);
        boolean visibled = tenantConfigService.getApprovalAllTapConfig(tenant.getRecId());

        SourcingRequestVisibleConfig sourcingRequestVisibleConfig = new SourcingRequestVisibleConfig();
        sourcingRequestVisibleConfig.setTenantId(tenantCode);
        sourcingRequestVisibleConfig.setVisibled(visibled);

        return sourcingRequestVisibleConfig;
    }

    @Override
    public EPAuthReviewerResponse getApproverListByConditions(ApproverSearchRequest request) {
        EPAuthUserSearchRequest epAuthUserSearchRequest = EPAuthUserSearchRequest.builder()
                .conditionSearchList(request.getConditionSearchList())
                .tenantId(request.getTenantId())
                .page(request.getPage())
                .pageSize(request.getPageSize())
                .sortBy(request.getSortBy())
                .sortOrder(request.getSortOrder())
                .build();

        String[] privileges = new String[] {PURCHASER.privilegeCode()};
        EPAuthUserListResponse epAuthUserListResponse = epAuthClient.getEPAuthUserList(privileges, epAuthUserSearchRequest);

        List<EPAuthReviewerDto> epAuthApproverDtoList = new ArrayList();

        if (epAuthUserListResponse.getData() == null) {
            return EPAuthReviewerResponse.builder()
                    .data(null)
                    .page(epAuthUserListResponse.getPage())
                    .pageSize(epAuthUserListResponse.getPageSize())
                    .total(epAuthUserListResponse.getTotal())
                    .totalPage(epAuthUserListResponse.getTotalPage())
                    .build();
        }

        for (EPAuthUserDTO epAuthUserDTO: epAuthUserListResponse.getData()) {
            epAuthApproverDtoList.add(
                    EPAuthReviewerDto.builder()
                            .sysUserId(epAuthUserDTO.getSysUserId())
                            .loginId(epAuthUserDTO.getLoginId())
                            .fullName(epAuthUserDTO.getFullName())
                            .email(epAuthUserDTO.getEmail())
                            .mobilePhone(epAuthUserDTO.getMobilePhone())
                            .phone(epAuthUserDTO.getPhone())
                            .timezone(epAuthUserDTO.getTimezone())
                            .build());
        }
        return EPAuthReviewerResponse.builder()
                .data(epAuthApproverDtoList)
                .page(epAuthUserListResponse.getPage())
                .pageSize(epAuthUserListResponse.getPageSize())
                .total(epAuthUserListResponse.getTotal())
                .totalPage(epAuthUserListResponse.getTotalPage())
                .build();
    }

    @Override
    public EPAuthPurchaserResponse getPurchaserListByConditions(PurchaserSearchRequest request, String[] privilegeCodes) {
        EPAuthUserSearchRequest epAuthUserSearchRequest = EPAuthUserSearchRequest.builder()
                .conditionSearchList(request.getConditionSearchList())
                .tenantId(request.getTenantId())
                .page(request.getPage())
                .pageSize(request.getPageSize())
                .sortBy(request.getSortBy())
                .sortOrder(request.getSortOrder())
                .build();

        EPAuthUserListResponse epAuthUserListResponse = new EPAuthUserListResponse();
        try {
            epAuthUserListResponse = epAuthClient.getEPAuthUserList(privilegeCodes, epAuthUserSearchRequest);
        } catch(Exception ex) {
            ex.getMessage();
        }
        List<EPAuthPurchaserDto> epAuthPurchaserDtoList = new ArrayList<>();

        if (epAuthUserListResponse.getData() == null) {
            return EPAuthPurchaserResponse.builder()
                    .data(null)
                    .page(epAuthUserListResponse.getPage())
                    .pageSize(epAuthUserListResponse.getPageSize())
                    .total(epAuthUserListResponse.getTotal())
                    .totalPage(epAuthUserListResponse.getTotalPage())
                    .build();
        }

        for (EPAuthUserDTO epAuthUserDTO: epAuthUserListResponse.getData()) {
            epAuthPurchaserDtoList.add(
                    EPAuthPurchaserDto.builder()
                            .sysUserId(epAuthUserDTO.getSysUserId())
                            .loginId(epAuthUserDTO.getLoginId())
                            .fullName(epAuthUserDTO.getFullName())
                            .email(epAuthUserDTO.getEmail())
                            .mobilePhone(epAuthUserDTO.getMobilePhone())
                            .phone(epAuthUserDTO.getPhone())
                            .timezone(epAuthUserDTO.getTimezone())
                            .build());
        }

        epAuthPurchaserDtoList.sort(Comparator.comparing(EPAuthPurchaserDto::getFullName));
        return EPAuthPurchaserResponse.builder()
                .data(epAuthPurchaserDtoList)
                .page(epAuthUserListResponse.getPage())
                .pageSize(epAuthUserListResponse.getPageSize())
                .total(epAuthUserListResponse.getTotal())
                .totalPage(epAuthUserListResponse.getTotalPage())
                .build();
    }

    @Override
    public List<RequestPurchaser> getRequestPurchaserByRequest(Request request) {
        return requestPurchaserRepository.findByRequest(request.getRecId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(Integer purchaserId, Request request) {
        Optional<RequestPurchaser> existingRequestPurchaser = requestPurchaserRepository.findTop1ByRequestId(request.getRecId());

        if (existingRequestPurchaser.isPresent()) {
            //if (existingRequestPurchaser.get().getPurchaser().getRecId().intValue() == purchaserId) return;
            if(request.getRequestStatus().getName().equals(TENANT_REQUEST_DRAFT.code()) ||
                    request.getRequestStatus().getName().equals(TENANT_REQUEST_PENDING.code())) {
                requestPurchaserRepository.delete(existingRequestPurchaser.get());
            }
        }

        if (purchaserId == null) return;

        Purchaser purchaser = purchaserRepository.findById(purchaserId)
                .orElseThrow(() -> new BusinessException(ApiMessage.E7086, ApiMessage.E7086.description()));

        RequestPurchaser requestPurchaser = null;

//        Optional<RequestPurchaser> matchRequestPurchaser = requestPurchaserRepository.findByRequestAndPurchaser(request, purchaser);
//        if(matchRequestPurchaser.isEmpty()) {
        if (request.getRequestStatus().getName().equals(TENANT_REQUEST_DRAFT.code()) ||
                request.getRequestStatus().getName().equals(TENANT_REQUEST_PENDING.code())) {

            requestPurchaser = RequestPurchaser.builder()
                    .id(new RequestPurchaserKey(request.getRecId(), purchaser.getRecId(), 1))
                    .request(request)
                    .purchaser(purchaser)
                    .workflowPermission(true)
                    .createdBy(request.getCreatedBy())
                    .createdDate(request.getCreatedDate())
                    .build();
        } else {
            List<RequestPurchaser> requestPurchasers = requestPurchaserRepository.findByRequest(request);
            Integer sequence = requestPurchasers.size() + 1;
            requestPurchaser = RequestPurchaser.builder()
                    .id(new RequestPurchaserKey(request.getRecId(), purchaser.getRecId(), sequence))
                    .request(request)
                    .purchaser(purchaser)
                    .workflowPermission(false)
                    .createdBy(AppUtil.getUserName())
                    .createdDate(DateTimeUtil.getTimestampUTC())
                    .build();
        }

        requestPurchaserRepository.save(requestPurchaser);
//        }
    }

    private List<InstanceApproverDto> getApprovers(String tenantId, String idp, Request request) {

        // Find instanceApproverId by ApproverName & DocumentId
        WorkflowInstanceApproverCriteria criteria = new WorkflowInstanceApproverCriteria();
        criteria.setPageNumber(1);
        criteria.setPageSize(100);

        List<String> refDocumentIds = new ArrayList<>();
        refDocumentIds.add(request.getRecId().toString());
        criteria.setRefDocumentId(refDocumentIds);

        List<String> statusList = new ArrayList<>();
        statusList.add("PENDING");
        statusList.add("AWAITING");
        statusList.add("APPROVED");
        statusList.add("REJECTED");
        statusList.add("FORWARDED");
        criteria.setStatus(statusList);

        // Optional Params
        List<Long> workflowInstanceIds = new ArrayList<>();
        workflowInstanceIds.add(request.getWorkflowInstanceId());
        criteria.setWorkflowInstanceId(workflowInstanceIds);

        Page<WorkflowInstApproverDto> approvers = workflowInstanceApprovalService.getByCriteria(criteria);

        List<InstanceApproverDto> instanceApprovers = approvers.getContent().stream()
                .map(item -> {
                    Map<String, UserDetailResponse> userDetailMap = new HashMap<>();
                    ContractDetailClientDto contractDetail = uaaService.getContractDetail(tenantId, idp, item.getReferApproverId(), userDetailMap);
                    if(contractDetail != null) {
                        InstanceApproverDto approver = new InstanceApproverDto();
                        approver.setSysUserId(Integer.parseInt(contractDetail.getUserId()));
                        approver.setLoginId(contractDetail.getUsername());
                        approver.setFullName(String.format("%s %s", contractDetail.getFirstName(), contractDetail.getLastName()));
                        approver.setEmail(contractDetail.getEmail());
                        approver.setMobilePhone(contractDetail.getMobilePhone());
                        approver.setPhone(contractDetail.getPhone());
                        approver.setStatus(item.getStatus());
                        approver.setRequired(item.getIsRequired());
                        approver.setComment(item.getRemark());
                        return approver;
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return instanceApprovers;
    }
}
