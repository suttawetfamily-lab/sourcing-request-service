package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.ExcSourcingStatusNameDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantExcSourcingStatus;
import com.pantavanij.sourcingreq.services.domain.mapper.ExcSourcingStatusMapper;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantExcSourcingStatusRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantConfigService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantExcSourcingStatusService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.ExcSourcingStatus.EXC_SOURCING_PENDING;
import static com.pantavanij.sourcingreq.services.enums.TenantApprovalStatus.*;

@RequiredArgsConstructor
@Service
public class TenantExcSourcingStatusServiceImpl implements TenantExcSourcingStatusService {

    private final TenantExcSourcingStatusRepository tenantExcSourcingStatusRepository;
    private final TenantConfigService tenantConfigService;
    private final TenantService tenantService;

    @Override
    public List<ExcSourcingStatusNameDto> getExcSourcingStatusList() {
        String tenantId = AppUtil.getTenantId();
        List<TenantExcSourcingStatus> excSourcingStatusList = tenantExcSourcingStatusRepository.findByTenant_Code(tenantId);
        excSourcingStatusList = excSourcingStatusList.stream().filter(l -> !Objects.equals(l.getExcSourcingStatus().getRecId(), EXC_SOURCING_PENDING.id())).collect(Collectors.toList());
        return ExcSourcingStatusMapper.INSTANCE.toExcSourcingStatusNameDto(excSourcingStatusList);
    }

    @Override
    public List<TenantExcSourcingStatus> getExcSourcingStatuses() {
        String tenantId = AppUtil.getTenantId();
        return tenantExcSourcingStatusRepository.findByTenant_Code(tenantId);
    }

    @Override
    public List<OptionDto> getExcSourcingStatusOptionDto(Integer tenantId, String searchTerm) {
        List<TenantExcSourcingStatus> requestStatusList = tenantExcSourcingStatusRepository.getTenantExcSourcingStatusByTenantIdAndSearchTerm(tenantId ,searchTerm);
        requestStatusList = requestStatusList.stream().filter(requestStatus -> !Objects.equals(requestStatus.getExcSourcingStatus().getRecId(), EXC_SOURCING_PENDING.id())).collect(Collectors.toList());
        return ExcSourcingStatusMapper.INSTANCE.tenantExcSourcingStatusToOptionDto(requestStatusList);
    }

    @Override
    public List<ExcSourcingStatusNameDto> getReviewStatusList() {
        String tenantId = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantId);
        List<String> excludedApprovalStatus;

        if(tenantConfigService.isEnableDeptApprover(tenant.getRecId())) {
            excludedApprovalStatus = Arrays.asList(TENANT_APPROVAL_DRAFT.code(), TENANT_APPROVAL_PENDING.code(), TENANT_APPROVAL_CANCELLED.code());
        } else {
            excludedApprovalStatus = Arrays.asList(TENANT_APPROVAL_DRAFT.code(), TENANT_APPROVAL_PENDING.code());
        }

        List<TenantExcSourcingStatus> requestStatusList =
                tenantExcSourcingStatusRepository.findByTenant_CodeAndNameNotIn(tenantId, excludedApprovalStatus);
        return ExcSourcingStatusMapper.INSTANCE.toExcSourcingStatusNameDto(requestStatusList);
    }
}
