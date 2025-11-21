package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.EPAuthReviewerDto;
import com.pantavanij.sourcingreq.services.domain.dto.ReportLineDto;
import com.pantavanij.sourcingreq.services.domain.dto.reviewer.RequestReportLineDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.ReportLineCommentRequest;
import com.pantavanij.sourcingreq.services.domain.request.ReportLineSearchRequest;
import com.pantavanij.sourcingreq.services.domain.response.*;

import java.util.List;

public interface RequestReportLineService {
    void saveRequestReportLine(Request request, Tenant tenant, List<Long> requestReportLines, EPAuthReviewerResponse response);
    List<EPAuthReviewerDto> getByRequest(Long requestId);
    ReportLineDto getDefaultReportLine();
    List<RequestReportLineDto> saveComment(ReportLineCommentRequest request);
    void delete(Request request);
    List<Long> findRequestByReportLineId(List<Long> reportLineId);
    List<RequestReportLineDto> getRequestReportLineByRequest(Long requestId);
    void deleteByRequestId(Long requestId);

    EPAuthReportLineResponse getReportLineListByConditions(ReportLineSearchRequest request, String[] privilegeCodes);
}
