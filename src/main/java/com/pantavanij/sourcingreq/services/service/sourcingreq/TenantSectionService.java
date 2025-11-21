package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.TenantSectionDto;
import com.pantavanij.sourcingreq.services.domain.dto.TenantSectionSearchDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantSection;
import com.pantavanij.sourcingreq.services.domain.request.TenantSectionRequest;
import com.pantavanij.sourcingreq.services.domain.request.TenantSectionSearchRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TenantSectionService {

    List<TenantSectionDto> getRequestFields(String privilegeCode, Integer organizationId);

    List<TenantSectionDto> getRequestItemFields(Integer requestTypeId, Integer organizationId);

    List<TenantSectionDto> getRequestItemHeaderFields(Integer requestTypeId, Integer organizationId);

    TenantSectionSearchDto searchTenantSectionByCondition(TenantSectionSearchRequest request, Pageable pageable);

    TenantSectionDto createNewTenantSection(TenantSectionRequest request, Integer organizationId);

    TenantSectionDto updateTenantSection(TenantSectionRequest request, Integer organizationId);

    Boolean deleteTenantSection(Integer tenantSectionId, Integer organizationId);

    TenantSectionDto getByRecId(Integer tenantSectionId);

    List<TenantSectionDto> getRequestItemFieldsForTemplateExcel(Integer requestTypeId, Integer organizationId);

    List<TenantSectionDto> getExistingPriceItemFields(Integer requestTypeId, Integer organizationId);

    List<TenantSectionDto> getExistingPriceItemHeaderFields(Integer requestTypeId, Integer organizationId);

    List<TenantSectionDto> getExceptionalSourcingItemFields(Integer requestTypeId, Integer organizationId);

    List<TenantSectionDto> getExceptionalSourcingItemHeaderFields(Integer requestTypeId, Integer organizationId);

    List<TenantSectionDto> getRequestReportFields(Integer organizationId);

    List<com.pantavanij.sourcingreq.services.domain.dto.TenantSectionDetailDto> getRequestPreviewFields(String mode, String pathUrl, Integer organizationId);

    List<TenantSection> getRequestItemReportFields(Integer organizationId);

    List<OptionDto> getOptionList(String optionName);

    List<TenantSectionDto> getRequestFullPreviewFields(Integer organizationId);
}
