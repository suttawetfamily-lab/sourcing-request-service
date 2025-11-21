package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.MasterGridFieldDto;
import com.pantavanij.sourcingreq.services.domain.dto.MasterGridFieldSearchableDto;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface MasterGridFieldService {

    List<MasterGridFieldDto> getMasterGridField(String privilegeCode, String tenantCode);

    List<MasterGridFieldSearchableDto> getMasterGridFieldSearchable(String privilegeCode, String tenantCode);

    @CachePut(value = "MasterGridFieldDtoList", key="#tenantCode")
    List<MasterGridFieldSearchableDto> refreshCache(String tenantCode);

    @Cacheable(value = "MasterGridFieldDtoList", key="#tenantCode")
    List<MasterGridFieldSearchableDto> getCache(String tenantCode);
}
