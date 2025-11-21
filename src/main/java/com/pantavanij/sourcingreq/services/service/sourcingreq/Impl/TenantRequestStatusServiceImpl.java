package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestStatusNameDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantRequestStatus;
import com.pantavanij.sourcingreq.services.domain.mapper.RequestStatusMapper;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantRequestStatusRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantConfigService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantRequestStatusService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.RequestStatus.REQUEST_PENDING;
import static com.pantavanij.sourcingreq.services.enums.TenantApprovalStatus.*;

@RequiredArgsConstructor
@Service
public class TenantRequestStatusServiceImpl implements TenantRequestStatusService {

    private final TenantRequestStatusRepository tenantRequestStatusRepository;
    private final TenantConfigService tenantConfigService;
    private final TenantService tenantService;

    @Override
    public List<RequestStatusNameDto> getRequestStatusList() {
        String tenantId = AppUtil.getTenantId();
        List<TenantRequestStatus> requestStatusList = tenantRequestStatusRepository.findByTenant_Code(tenantId);
        requestStatusList = requestStatusList.stream().filter(l -> !Objects.equals(l.getRequestStatus().getRecId(), REQUEST_PENDING.id())).collect(Collectors.toList());
        return RequestStatusMapper.INSTANCE.toRequestStatusNameDto(requestStatusList);
    }

    @Override
    public List<TenantRequestStatus> getRequestStatuses() {
        String tenantId = AppUtil.getTenantId();
        return tenantRequestStatusRepository.findByTenant_Code(tenantId);
    }

    @Override
    public List<OptionDto> getRequestStatusOptionDto(Integer tenantId, String searchTerm) {
        List<TenantRequestStatus> requestStatusList = tenantRequestStatusRepository.getTenantRequestStatusByTenantIdAndSearchTerm(tenantId ,searchTerm);
        requestStatusList = requestStatusList.stream().filter(requestStatus -> !Objects.equals(requestStatus.getRequestStatus().getRecId(), REQUEST_PENDING.id())).collect(Collectors.toList());
        return RequestStatusMapper.INSTANCE.tenantRequestStatusToOptionDto(requestStatusList);
    }

    @Override
    public List<RequestStatusNameDto> getReviewStatusList() {
        String tenantId = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantId);
        List<String> excludedApprovalStatus;

        if(tenantConfigService.isEnableDeptApprover(tenant.getRecId())) {
            excludedApprovalStatus = Arrays.asList(TENANT_APPROVAL_DRAFT.code(), TENANT_APPROVAL_PENDING.code(), TENANT_APPROVAL_CANCELLED.code());
        } else {
            excludedApprovalStatus = Arrays.asList(TENANT_APPROVAL_DRAFT.code(), TENANT_APPROVAL_PENDING.code());
        }

        List<TenantRequestStatus> requestStatusList =
                tenantRequestStatusRepository.findByTenant_CodeAndNameNotIn(tenantId, excludedApprovalStatus);
        return RequestStatusMapper.INSTANCE.toRequestStatusNameDto(requestStatusList);
    }
}
