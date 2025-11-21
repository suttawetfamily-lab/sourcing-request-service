package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.ExcSourcingStatusNameDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantExcSourcingStatus;

import java.util.List;

public interface TenantExcSourcingStatusService {

    List<ExcSourcingStatusNameDto> getExcSourcingStatusList();

    List<TenantExcSourcingStatus> getExcSourcingStatuses();

    List<OptionDto> getExcSourcingStatusOptionDto(Integer recId, String searchTerm);

    List<ExcSourcingStatusNameDto> getReviewStatusList();
}
