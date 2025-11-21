package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ReportLine;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.ReportLineSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthReviewerResponse;
import com.pantavanij.sourcingreq.services.domain.response.ReportLineResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReportLineService {
    ReportLineResponse getRequestReportLine(ReportLineSearchRequest request, Pageable pageable);

    EPAuthReviewerDto getDefaultReportLine();
    Long saveReportLine(ReportLineDto reportLineDto, Tenant tenant);
    EPAuthReviewerResponse getReportLineListByConditions(ReportLineSearchRequest request, List<Long> requestReportLines);

    ReportLineSearchDto searchReportLineListByConditions(ReportLineSearchRequest request, Pageable pageable);

    ReportLineDto findReportLineByRecId(Integer reportLineId);

    List<ReportLine> findByReportLineName(String reportLineName);

    ReportLineDto createReportLine(ReportLineRequest reportLineRequest);

    ReportLineDto updateReportLine(ReportLineRequest reportLineRequest);

    boolean deleteReportLine(Integer reportLineId);

    ReportLineDto updateReportLineSequence(SequenceRequest request);
}
