package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.TenantDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TenantResponse {
    private ApiResponseStatus status;

    private TenantDto data;

    public TenantResponse(TenantDto tenantDto) {
        this.data = tenantDto;
        this.status = new ApiResponseStatus();
    }
}
