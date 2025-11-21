package com.pantavanij.sourcingreq.services.domain.dto;

import com.pantavanij.sourcingreq.services.domain.dto.deptapprover.DeptApproverRequestDto;
import lombok.Data;

import java.util.List;

@Data
public class ExcSourcingDeptApproverSearchDto {
    private List<DeptApproverRequestDto> requestDtoList;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}
