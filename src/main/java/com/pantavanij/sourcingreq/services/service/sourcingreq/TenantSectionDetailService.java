package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.request.*;

import java.util.List;

public interface TenantSectionDetailService {
    List<TenantSectionDetailDto> getExportRequestRpt(Integer tenantId, Integer requestReportId, Integer organizationId);
    List<TenantSectionDetailDto> getExportRequestItemRpt(Integer tenantId, Integer requestItemReportId, Integer organizationId);
    boolean isRequiredField(Integer tenantId, String fieldName, Integer validatorId, String sectionType);
    TenantSectionDetailDto createTenantSectionDetail(TenantSectionDetailRequest request);
    TenantSectionDetailDto updateTenantSectionDetail(TenantSectionDetailRequest request);
    List<TenantSectionDetailFieldNameDto> getTenantSectionDetailFieldName(Integer tenantSectionId, Integer organizationId);
    List<TenantSectionDetailFieldNameDto> getTenantSectionDetailFieldNameByTypes(String tenantSectionTypes, Integer organizationId);
}
