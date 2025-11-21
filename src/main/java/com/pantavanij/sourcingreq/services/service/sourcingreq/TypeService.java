package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import org.springframework.data.domain.*;

import javax.validation.*;
import java.util.List;

public interface TypeService {
    List<OptionDto> getTypeByTenantId(Integer tenantId);
    TypeDto getByTypeId(Integer typeId);
    TypeSearchDto searchTypeListByCondition(TypeSearchRequest request, Pageable pageable);
    Integer createType(TypeRequest request);
    Integer updateType(TypeRequest request);
    TypeDto updateTypeSequence(@Valid SequenceRequest request);
}
