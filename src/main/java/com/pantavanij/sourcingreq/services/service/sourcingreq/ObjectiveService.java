package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;

import java.util.List;

public interface ObjectiveService {

    List<OptionDto> getObjectiveByTenantIdAndSearchTerm(Integer tenantId, String searchTerm);
}
