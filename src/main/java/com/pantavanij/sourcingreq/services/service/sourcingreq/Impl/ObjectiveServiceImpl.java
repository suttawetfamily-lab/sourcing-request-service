package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Objective;
import com.pantavanij.sourcingreq.services.domain.mapper.ObjectiveMapper;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.ObjectiveRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.ObjectiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ObjectiveServiceImpl implements ObjectiveService {

    private final ObjectiveRepository objectiveRepository;

    @Override
    public List<OptionDto> getObjectiveByTenantIdAndSearchTerm(Integer tenantId, String searchTerm) {
        List<Objective> objectives = objectiveRepository.getObjectiveByTenantIdAndSearchTerm(tenantId, searchTerm.trim());
        return ObjectiveMapper.INSTANCE.toObjectiveOptionDto(objectives);
    }
}
