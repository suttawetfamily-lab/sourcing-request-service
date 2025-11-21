package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.RequestStatusDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.mapper.RequestStatusMapper;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestStatusRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantApprovalStatusRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantRequestStatusRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestStatusService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.SourcingStatus.*;
import static com.pantavanij.sourcingreq.services.enums.TenantApprovalStatus.TENANT_APPROVAL_AWAITING;
import static com.pantavanij.sourcingreq.services.enums.TenantApprovalStatus.TENANT_APPROVAL_PARTIAL_COMPLETED;
import static com.pantavanij.sourcingreq.services.enums.TenantRequestStatus.TENANT_REQUEST_COMPLETED;
import static com.pantavanij.sourcingreq.services.enums.TenantRequestStatus.TENANT_REQUEST_PARTIAL_COMPLETED;

@RequiredArgsConstructor
@Service
public class RequestStatusServiceImpl implements RequestStatusService {

    private final RequestStatusRepository requestStatusRepository;
    private final RequestRepository requestRepository;
    private final TenantApprovalStatusRepository tenantApprovalStatusRepository;
    private final TenantRequestStatusRepository tenantRequestStatusRepository;
    private final UaaService uaaService;

    @Override
    public List<RequestStatusDto> getRequestStatusList() {
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        List<RequestStatus> requestStatusList = requestStatusRepository.findAll();
        return RequestStatusMapper.INSTANCE.toRequestStatusDtoList(requestStatusList, timeZone);
    }

    @Override
    public void updateRequestAndApprovalStatus(List<RequestItem> requestItemList, Request request) {
        TenantRequestStatus tenantRequestStatus;
        TenantApprovalStatus tenantApprovalStatus;

        if(TENANT_REQUEST_COMPLETED.code().equals(request.getRequestStatus().getName()) ||
                TENANT_REQUEST_PARTIAL_COMPLETED.code().equals(request.getRequestStatus().getName())){

            Long completelySourcing = requestItemList.stream().filter(r ->
                    r.getSourcingStatus().getRecId() == SOURCING_QUALIFIED_SUPPLIER.id() ||
                    r.getSourcingStatus().getRecId() == SOURCING_CANCELLED.id() ||
                    r.getSourcingStatus().getRecId() == SOURCING_REJECTED.id() ||
                    r.getSourcingStatus().getRecId() == SOURCING_DELETED.id() ||
                    r.getSourcingStatus().getRecId() == SOURCING_NO_QUALIFIED_SUPPLIER.id() ||
                    r.getSourcingStatus().getRecId() == SOURCING_NO_SUPPLIER_RESPONSE.id() ||
                    r.getSourcingStatus().getRecId() == SOURCING_NO_SUPPLIER_SELECTED.id()).count();

            // If All Items status are Qualified Supplier, the Request status will be REQUEST_COMPLETED
            if (completelySourcing == requestItemList.size()) {
                tenantRequestStatus = tenantRequestStatusRepository.findByNameAndTenant(TENANT_REQUEST_COMPLETED.code(), request.getTenant());
                request.setStatusId(tenantRequestStatus.getRequestStatus().getRecId());
                request.setRequestStatus(tenantRequestStatus);
            }
            // If some of Items status are Qualified Supplier, the Request status will be REQUEST_PARTIAL_COMPLETED
            else {
                tenantRequestStatus = tenantRequestStatusRepository.findByNameAndTenant(TENANT_REQUEST_PARTIAL_COMPLETED.code(), request.getTenant());
                request.setStatusId(tenantRequestStatus.getRequestStatus().getRecId());
                request.setRequestStatus(tenantRequestStatus);
            }
        }

        if(TENANT_APPROVAL_AWAITING.code().equals(request.getApprovalStatus().getName()) ||
                TENANT_APPROVAL_PARTIAL_COMPLETED.code().equals(request.getApprovalStatus().getName())){

            Long awaitingSourcing = requestItemList.stream().filter(r ->
                    r.getSourcingStatus().getRecId() == SOURCING_NONE.id() ||
                            r.getSourcingStatus().getRecId() == SOURCING_DELETED.id()).count();

            // If All Items status are None or DELETED, the Approval status will be APPROVAL_AWAITING
            if (awaitingSourcing == requestItemList.size()) {
                tenantApprovalStatus = tenantApprovalStatusRepository.findByNameAndTenant(TENANT_APPROVAL_AWAITING.code(), request.getTenant());
                request.setApprovalStatusId(tenantApprovalStatus.getApprovalStatus().getRecId());
                request.setApprovalStatus(tenantApprovalStatus);
            } else {
                tenantApprovalStatus = tenantApprovalStatusRepository.findByNameAndTenant(TENANT_APPROVAL_PARTIAL_COMPLETED.code(), request.getTenant());
                request.setApprovalStatusId(tenantApprovalStatus.getApprovalStatus().getRecId());
                request.setApprovalStatus(tenantApprovalStatus);
            }
        }

        requestRepository.save(request);
    }

    @Override
    public void updateRequestAndApprovalStatus(List<RequestItem> requestItemList, List<Request> requestList) {
        for(Request request : requestList) {
            List<RequestItem> filteredRequestItemLst = requestItemList.stream().filter(ri -> ri.getRequest().equals(request)).collect(Collectors.toList());
            this.updateRequestAndApprovalStatus(filteredRequestItemLst, request);
        }
    }
}
