package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Unit;
import com.pantavanij.sourcingreq.services.domain.request.*;
import org.springframework.data.domain.*;

import javax.validation.*;
import java.util.List;
import java.util.Optional;

public interface UnitService {
    List<UnitDto> getUnitByTenantIdV1(Integer tenantId, Integer organizationId);

    List<UnitDto> getUnitSearchTermV1(Integer tenantId, String searchTerm, Integer organizationId);

    List<OptionDto> getUnitSearchTerm(Integer tenantId, String searchTerm, Integer organizationId);

    Optional<Unit> getUnitByUnitCode(String unitCode);

    Unit createUnit(UnitDto unitDto);

    List<OptionDto> getAllUnit();

    Integer createUnit(UnitMasterDataRequest request);

    UnitMasterDataDto getUnitByUnitId(Integer unitId);

    UnitSearchDto searchUnitsByCondition(UnitSearchRequest request, Pageable pageable);

    Integer updateUnit(@Valid UnitMasterDataRequest request);

    Integer deleteUnitById(Integer unitId);
}
