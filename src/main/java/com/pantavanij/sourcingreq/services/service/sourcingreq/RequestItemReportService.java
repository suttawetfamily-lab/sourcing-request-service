package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.RequestItemReportDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestItemReportSearchDto;
import com.pantavanij.sourcingreq.services.domain.request.RequestItemReportRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestItemReportSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.SequenceRequest;
import com.pantavanij.sourcingreq.services.domain.response.RequestItemReportResponse;
import org.springframework.data.domain.Pageable;

public interface RequestItemReportService {

    RequestItemReportResponse getRequestRequestItemReport(RequestItemReportSearchRequest request, Pageable pageable);

    RequestItemReportDto createRequestItemReport(RequestItemReportRequest request, Integer organizationId);

    RequestItemReportDto updateRequestItemReport(RequestItemReportRequest request, Integer organizationId);

    RequestItemReportSearchDto searchRequestItemReportListByCondition(RequestItemReportSearchRequest request, Pageable pageable, Integer organizationId);

    boolean deleteRequestItemReport(Integer requestItemReportId);

    RequestItemReportDto findRequestItemReportByRecId(Integer requestItemReportId);

    RequestItemReportDto updateRequestItemReportSequence(SequenceRequest request);

    Integer saveRequestItemReport(RequestItemReportDto requestItemReportDto);
}
