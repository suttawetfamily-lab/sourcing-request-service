package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.TenantRequestReportDto;
import com.pantavanij.sourcingreq.services.domain.dto.TenantRequestReportSearchDto;
import com.pantavanij.sourcingreq.services.domain.request.RequestReportRequest;
import com.pantavanij.sourcingreq.services.domain.request.SequenceRequest;
import com.pantavanij.sourcingreq.services.domain.request.TenantRequestReportSearchRequest;
import org.springframework.data.domain.Pageable;

import javax.validation.Valid;

public interface TenantRequestReportService {
    TenantRequestReportDto getByRequestReportId(Integer requestReportId);
    Integer createTenantRequestReport(RequestReportRequest request);
    Integer updateTenantRequestReport(RequestReportRequest request);
    TenantRequestReportSearchDto searchTenantRequestReportByCondition(@Valid TenantRequestReportSearchRequest request, Pageable pageable);
    TenantRequestReportDto updateTenantRequestReportSequence(SequenceRequest request);
}
