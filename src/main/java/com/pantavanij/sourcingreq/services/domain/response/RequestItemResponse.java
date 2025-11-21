package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.RequestItemDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestItemResponse {
    private ApiResponseStatus status;

    private Long requestItemId;

    public RequestItemResponse(RequestItemDto requestItemDto) {
        this.requestItemId = requestItemDto.getRecId();
        this.status = new ApiResponseStatus();
    }
}
