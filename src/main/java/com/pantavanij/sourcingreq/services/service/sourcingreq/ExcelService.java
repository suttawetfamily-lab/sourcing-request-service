package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.ExcelRequestItemDto;
import com.pantavanij.sourcingreq.services.domain.dto.requester.RequesterRequestDto;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface ExcelService {
    String generateRequestReport(List<RequesterRequestDto> requestList, Integer requestReportId, Integer organizationId) throws IOException;
    String generateRequestItemReport(List<ExcelRequestItemDto> excelDataList, String pathURL, Integer requestItemReportId, Integer organizationId) throws IOException, ParseException;
    String generateRequestItemTemplate(Integer requestTypeId, Integer type, Integer organizationId) throws IOException, ParseException;
}
