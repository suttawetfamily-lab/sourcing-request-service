package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.TenantRequestItemReportDto;
import com.pantavanij.sourcingreq.services.domain.dto.TenantRequestItemReportSearchDto;
import com.pantavanij.sourcingreq.services.domain.request.SequenceRequest;
import com.pantavanij.sourcingreq.services.domain.request.TenantRequestItemReportRequest;
import com.pantavanij.sourcingreq.services.domain.request.TenantRequestItemReportSearchRequest;
import org.springframework.data.domain.Pageable;

import javax.validation.Valid;
import java.util.List;

public interface TenantRequestItemReportService {

    List<TenantRequestItemReportDto> getByRequestItemReportId(Integer requestItemReportId);

    List<TenantRequestItemReportDto> getByRequestItemReportId(Integer requestItemReportId, Integer organizationId);

    TenantRequestItemReportDto createTenantRequestItemReport(TenantRequestItemReportRequest request);

    TenantRequestItemReportDto createTenantRequestItemReport(TenantRequestItemReportRequest request, Integer organizationId);

    Integer updateTenantRequestItemReport(TenantRequestItemReportRequest request);

    Integer updateTenantRequestItemReport(TenantRequestItemReportRequest request, Integer organizationId);

    TenantRequestItemReportSearchDto searchTenantRequestItemReportByCondition(
            @Valid TenantRequestItemReportSearchRequest request, Pageable pageable);

    TenantRequestItemReportSearchDto searchTenantRequestItemReportByCondition(
            @Valid TenantRequestItemReportSearchRequest request, Pageable pageable, Integer organizationId);

    TenantRequestItemReportDto updateTenantRequestItemReportSequence(SequenceRequest request);


    TenantRequestItemReportDto updateTenantRequestItemReportSequence(SequenceRequest request, Integer organizationId);
}
