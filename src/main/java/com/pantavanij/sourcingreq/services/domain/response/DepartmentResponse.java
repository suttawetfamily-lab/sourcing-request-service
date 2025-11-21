package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.DepartmentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentResponse {
    private ApiResponseStatus status;

    private List<DepartmentDto> departmentList;

    public DepartmentResponse(List<DepartmentDto> departmentList) {
        this.departmentList = departmentList;
        this.status = new ApiResponseStatus();
    }
}
