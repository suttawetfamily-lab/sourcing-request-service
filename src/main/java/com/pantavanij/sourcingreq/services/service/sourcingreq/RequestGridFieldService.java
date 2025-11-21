package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.RequestGridFieldDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestGridFieldSearchDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestGridFieldSearchableDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestItemGridFieldDto;
import com.pantavanij.sourcingreq.services.domain.request.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RequestGridFieldService {

    List<RequestGridFieldDto> getRequestGridField(String privilegeCode, String tenantCode, Integer organizationId);

    List<RequestGridFieldSearchableDto> getRequestGridFieldSearchable(String privilegeCode, String tenantCode, Integer organizationId);

    RequestGridFieldSearchDto searchRequestGridFieldByCondition(RequestGridFieldSearchRequest searchRequest, Pageable pageable);

    RequestGridFieldDto findRequestGridFieldByRecIdAndTenantIdAndPrivilegeCode(Integer requestGridFieldId, String privilegeCode);

    RequestGridFieldDto createRequestGridField(RequestGridFieldRequest request);

    RequestGridFieldDto updateRequestGridField(RequestGridFieldRequest request);

    RequestGridFieldDto updateRequestGridFieldSequence(RequestGridFieldSequenceRequest request);

//    @CachePut(value = "requestGridFieldDtoList", key="#tenantCode")
//    List<RequestGridFieldSearchableDto> refreshCache(String tenantCode);
//
//    @Cacheable(value = "requestGridFieldDtoList", key="#tenantCode")
//    List<RequestGridFieldSearchableDto> getCache(String tenantCode);
}
