package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.ContractDetailClientDto;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestAssignResponse {
    private ApiResponseStatus status;

    private ContractDetailClientDto assignedUser;

    public RequestAssignResponse(ContractDetailClientDto assignedUser, ApiMessage apiMessage, String description) {
        this.assignedUser = assignedUser;
        this.status = new ApiResponseStatus(apiMessage, description);
    }
}
