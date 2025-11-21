package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestStatusNameDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;

import java.util.List;

public interface TenantRequestStatusService {

    List<RequestStatusNameDto> getRequestStatusList();

    List<TenantRequestStatus> getRequestStatuses();

    List<OptionDto> getRequestStatusOptionDto(Integer recId, String searchTerm);

    List<RequestStatusNameDto> getReviewStatusList();
}
