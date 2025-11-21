package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.RequestItemGridFieldDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestItemGridFieldSearchDto;
import com.pantavanij.sourcingreq.services.domain.request.RequestItemGridFieldRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestItemGridFieldSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestItemGridFieldSequenceRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RequestItemGridFieldService {

    List<RequestItemGridFieldDto> getRequestItemGridField(String privilegeCode, String tenantCode, Integer typeId, Integer organizationId);

    RequestItemGridFieldSearchDto searchRequestItemGridFieldByCondition(RequestItemGridFieldSearchRequest searchRequest,
                                                                        Pageable pageable);

    RequestItemGridFieldDto findRequestItemGridFieldByRecIdAndTenantIdAndPrivilegeCode(Integer requestItemGridFieldId,
                                                                                       String privilegeCode,
                                                                                       Integer organizationId);

    RequestItemGridFieldDto createRequestItemGridField(RequestItemGridFieldRequest request);

    RequestItemGridFieldDto updateRequestItemGridField(RequestItemGridFieldRequest request);

    RequestItemGridFieldDto updateRequestItemGridFieldSequence(RequestItemGridFieldSequenceRequest request);
}
