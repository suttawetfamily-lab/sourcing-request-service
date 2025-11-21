package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantUnit;
import com.pantavanij.sourcingreq.services.domain.request.*;
import org.springframework.data.domain.*;

import javax.validation.*;
import java.util.List;
import java.util.Optional;

public interface TenantUnitService {
    void createTenantUnit(TenantUnitDto tenantUnitDto);

    Integer createTenantUnit(UnitRequest request);

    TenantUnit findTenantUnitByUnitIdAndTenantId(Integer unitId, Integer tenantId);

    TenantUnitDto getByUnitId(Integer unitId);

    TenantUnitSearchDto searchTenantUnitByCondition(UnitSearchRequest request, Pageable pageable);

    Integer updateTenantUnit(UnitRequest request);

    TenantUnitDto updateTenantUnitSequence(SequenceRequest request);

    Integer deleteTenantUnitById(Integer unitId);

    List<OptionDto> getTenantUnitByTenantIdAndSearchTerm(Integer tenantId, Integer organizationId, String searchTerm);


}
