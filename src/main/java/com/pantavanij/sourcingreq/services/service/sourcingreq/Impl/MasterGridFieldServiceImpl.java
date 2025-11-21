package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.MasterGridFieldDto;
import com.pantavanij.sourcingreq.services.domain.dto.MasterGridFieldSearchableDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantMasterGridField;
import com.pantavanij.sourcingreq.services.domain.mapper.TenantMasterGridFieldMapper;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantMasterGridFieldRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.MasterGridFieldService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.MasterGridFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MasterGridFieldServiceImpl implements MasterGridFieldService {

    private final TenantMasterGridFieldRepository tenantMasterGridFieldRepository;

    @Override
    public List<MasterGridFieldDto> getMasterGridField(String groupName, String tenantCode) {

        List<TenantMasterGridField> tenantMasterGridFieldList =
                tenantMasterGridFieldRepository.findByGroupNameAndTenant_CodeOrderBySequence(groupName, tenantCode);

        List<TenantMasterGridField> visibleList = tenantMasterGridFieldList
                .stream()
                .filter(TenantMasterGridField::isVisible)
                .collect(Collectors.toList());

        return TenantMasterGridFieldMapper.INSTANCE.toMasterGridFieldDto(visibleList);
    }

    @Override
    public List<MasterGridFieldSearchableDto> getMasterGridFieldSearchable(String groupName, String tenantCode) {

        List<TenantMasterGridField> tenantMasterGridFieldList =
                tenantMasterGridFieldRepository.findByGroupNameAndTenant_CodeAndSearchableOrderBySequence(
                        groupName, tenantCode, true);

        return TenantMasterGridFieldMapper.INSTANCE.toMasterGridFieldSearchableDto(tenantMasterGridFieldList);
    }
    @Override
    @CachePut(value = "masterGridFieldDtoList", key="#tenantCode")
    public List<MasterGridFieldSearchableDto> refreshCache(String tenantCode) {
        List<TenantMasterGridField> tenantMasterGridFieldList =
                tenantMasterGridFieldRepository.findByTenant_CodeAndSearchableOrderBySequence(tenantCode,true);
        return TenantMasterGridFieldMapper.INSTANCE.toMasterGridFieldSearchableDto(tenantMasterGridFieldList);
    }
    @Override
    @Cacheable(value = "masterGridFieldDtoList", key="#tenantCode")
    public List<MasterGridFieldSearchableDto> getCache(String tenantCode) {
        List<TenantMasterGridField> tenantMasterGridFieldList =
                tenantMasterGridFieldRepository.findByTenant_CodeAndSearchableOrderBySequence(tenantCode, true);
        return TenantMasterGridFieldMapper.INSTANCE.toMasterGridFieldSearchableDto(tenantMasterGridFieldList);
    }

}
