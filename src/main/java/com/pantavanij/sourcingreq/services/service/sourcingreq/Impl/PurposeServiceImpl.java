package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.PurposeDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Purpose;
import com.pantavanij.sourcingreq.services.domain.mapper.PurposeMapper;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.PurposeRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.PurposeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PurposeServiceImpl implements PurposeService {

    private final PurposeRepository purposeRepository;

    public List<OptionDto> getPurposeByTenantIdAndSearchTerm(Integer tenantId,String searchTerm) {
        List<Purpose> purposeList = purposeRepository.getPurposeByTenantIdAndSearchTerm(tenantId,searchTerm);
        return PurposeMapper.INSTANCE.toPurposeOptionDto(purposeList);
    }
    @Override
    public List<PurposeDto> getPurposeByTenantId (Integer tenantId){
        List<Purpose> purposeList = purposeRepository.getPurposeByTenantId(tenantId);
        return PurposeMapper.INSTANCE.toPurposeDtoList(purposeList);
    }
}
