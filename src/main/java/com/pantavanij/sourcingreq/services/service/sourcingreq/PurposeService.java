package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.PurposeDto;

import java.util.List;

public interface PurposeService {

    List<OptionDto> getPurposeByTenantIdAndSearchTerm(Integer tenantId,String searchTerm);
     List<PurposeDto> getPurposeByTenantId (Integer tenantId);
}
