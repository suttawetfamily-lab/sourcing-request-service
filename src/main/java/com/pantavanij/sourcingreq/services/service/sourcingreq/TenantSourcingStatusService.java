package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantSourcingStatus;
import com.pantavanij.sourcingreq.services.domain.request.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.validation.Valid;
import java.util.List;

public interface TenantSourcingStatusService {
    TenantSourcingStatusDto getBySourcingStatusIdAndTenant(Integer sourcingStatusId);
    TenantSourcingStatusDto updateTenantSourcingStatus(TenantSourcingStatusRequest request);
    TenantSourcingStatusSearchDto searchTenantSourcingStatusByCondition(@Valid TenantSourcingStatusSearchRequest request, Pageable pageable);
    TenantSourcingStatusDto updateTenantSourcingStatusSequence(SequenceRequest request);
}
