package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.SourcingRequestVisibleConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SourcingRequestVisibleConfigResponse {
    private ApiResponseStatus status;
    private SourcingRequestVisibleConfig data;

    public SourcingRequestVisibleConfigResponse(SourcingRequestVisibleConfig sourcingRequestVisibleConfig) {
        this.data = sourcingRequestVisibleConfig;
        this.status = new ApiResponseStatus();
    }
}
