package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SourcingStatus;
import com.pantavanij.sourcingreq.services.domain.mapper.SourcingStatusMapper;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.SourcingStatusRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.SourcingStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SourcingStatusServiceImpl implements SourcingStatusService {

    private final SourcingStatusRepository sourcingStatusRepository;

    @Override
    public SourcingStatus getSourcingStatusById(Integer recId) {
        return sourcingStatusRepository.findSourcingStatusByRecId(recId);
    }

    @Override
    public List<OptionDto> getRequestStatusOptionDto(String searchTerm) {
        List<SourcingStatus> sourcingStatuses = sourcingStatusRepository.findBydSearchTerm(searchTerm);
        return SourcingStatusMapper.INSTANCE.sourcingStatusToOptionDtoList(sourcingStatuses);
    }

}
