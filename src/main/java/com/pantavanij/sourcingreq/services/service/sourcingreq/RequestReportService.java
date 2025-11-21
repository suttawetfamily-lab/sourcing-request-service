package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.RequestReportDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestReportSearchDto;
import com.pantavanij.sourcingreq.services.domain.request.RequestReportRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestReportSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.SequenceRequest;
import com.pantavanij.sourcingreq.services.domain.response.RequestReportResponse;
import org.springframework.data.domain.Pageable;

public interface RequestReportService {
    RequestReportResponse getRequestReport(RequestReportSearchRequest request, Pageable pageable);

    Integer saveRequestReport(RequestReportDto requestReportDto);

    RequestReportSearchDto searchRequestReportListByCondition(RequestReportSearchRequest request, Pageable pageable);

    RequestReportDto findRequestReportByRecId(Integer requestReportId);

    RequestReportDto createRequestReport(RequestReportRequest requestReportRequest);

    RequestReportDto updateRequestReport(RequestReportRequest requestReportRequest);

    boolean deleteRequestReport(Integer requestReportId);

    RequestReportDto updateRequestReportSequence(SequenceRequest request);
}
