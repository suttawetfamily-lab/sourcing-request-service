package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.SourcingGridFieldDto;
import com.pantavanij.sourcingreq.services.domain.dto.SourcingGridFieldSearchDto;
import com.pantavanij.sourcingreq.services.domain.dto.SourcingGridFieldSearchableDto;
import com.pantavanij.sourcingreq.services.domain.request.SourcingGridFieldRequest;
import com.pantavanij.sourcingreq.services.domain.request.SourcingGridFieldSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.SourcingGridFieldSequenceRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SourcingGridFieldService {

    List<SourcingGridFieldDto> getSourcingGridField(String privilegeCode, String tenantCode);

    List<SourcingGridFieldSearchableDto> getSourcingGridFieldSearchable(String privilegeCode, String tenantCode);

    SourcingGridFieldSearchDto searchSourcingGridFieldByCondition(SourcingGridFieldSearchRequest searchRequest, Pageable pageable);

    SourcingGridFieldDto findSourcingGridFieldByRecIdAndTenantIdAndPrivilegeCode(Integer requestGridFieldId, String privilegeCode);

    SourcingGridFieldDto createSourcingGridField(SourcingGridFieldRequest request);

    SourcingGridFieldDto updateSourcingGridField(SourcingGridFieldRequest request);

    SourcingGridFieldDto updateSourcingGridFieldSequence(SourcingGridFieldSequenceRequest request);

//    @CachePut(value = "requestGridFieldDtoList", key="#tenantCode")
//    List<SourcingGridFieldSearchableDto> refreshCache(String tenantCode);
//
//    @Cacheable(value = "requestGridFieldDtoList", key="#tenantCode")
//    List<SourcingGridFieldSearchableDto> getCache(String tenantCode);
}
