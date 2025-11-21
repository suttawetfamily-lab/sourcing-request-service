package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.ApprovalStatusNameDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantApprovalStatus;
import com.pantavanij.sourcingreq.services.domain.mapper.ApprovalStatusMapper;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantApprovalStatusRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantApprovalStatusService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantConfigService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.pantavanij.sourcingreq.services.enums.TenantApprovalStatus.*;

@RequiredArgsConstructor
@Service
public class TenantApprovalStatusServiceImpl implements TenantApprovalStatusService {

    private final TenantApprovalStatusRepository tenantApprovalStatusRepository;
    private final TenantConfigService tenantConfigService;
    private final TenantService tenantService;

    @Override
    public List<ApprovalStatusNameDto> getApprovalStatusSearchList() {
        String tenantId = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantId);
        List<String> excludedRequestStatus;
        if(tenantConfigService.isEnableDeptApprover(tenant.getRecId())) {
            excludedRequestStatus = Arrays.asList(TENANT_APPROVAL_DRAFT.code(), TENANT_APPROVAL_PENDING.code(), TENANT_APPROVAL_CANCELLED.code());
        } else {
            excludedRequestStatus = Arrays.asList(TENANT_APPROVAL_DRAFT.code(), TENANT_APPROVAL_PENDING.code());
        }

        List<TenantApprovalStatus> approvalStatusList =
                tenantApprovalStatusRepository.findByTenant_CodeAndNameNotIn(tenantId, excludedRequestStatus);
        return ApprovalStatusMapper.INSTANCE.toApprovalStatusNameDto(approvalStatusList);
    }

    @Override
    public List<TenantApprovalStatus> getApprovalStatus() {
        String tenantId = AppUtil.getTenantId();
        List<String> excludedRequestStatus = Arrays.asList(TENANT_APPROVAL_DRAFT.code(), TENANT_APPROVAL_PENDING.code());
        return tenantApprovalStatusRepository.findByTenant_CodeAndNameNotIn(tenantId, excludedRequestStatus);
    }

}
