package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.ReportLineDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ReportLine;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ReportLineMapper {
    ReportLineMapper INSTANCE = Mappers.getMapper(ReportLineMapper.class);

    ReportLineDto toRequestReportLineDto(ReportLine requestReportLine);

    List<ReportLineDto> toRequestReportLineDtoList(List<ReportLine> requestReportLines);
}
