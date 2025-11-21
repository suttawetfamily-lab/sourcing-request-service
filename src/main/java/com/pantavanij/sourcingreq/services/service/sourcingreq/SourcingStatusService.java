package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SourcingStatus;

import java.util.List;

public interface SourcingStatusService {
    SourcingStatus getSourcingStatusById(Integer recId);

    List<OptionDto> getRequestStatusOptionDto(String searchTerm);
}
