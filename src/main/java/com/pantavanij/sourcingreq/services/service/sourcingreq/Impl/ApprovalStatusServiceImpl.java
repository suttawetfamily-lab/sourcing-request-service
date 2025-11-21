package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.ApprovalStatusDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ApprovalStatus;
import com.pantavanij.sourcingreq.services.domain.mapper.ApprovalStatusMapper;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.ApprovalStatusRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.ApprovalStatusService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static com.pantavanij.sourcingreq.services.enums.ApprovalStatus.APPROVAL_CANCELLED;
import static com.pantavanij.sourcingreq.services.enums.ApprovalStatus.APPROVAL_DRAFT;

@RequiredArgsConstructor
@Service
public class ApprovalStatusServiceImpl implements ApprovalStatusService {

    private final ApprovalStatusRepository approvalStatusRepository;
    private final UaaService uaaService;

    @Override
    public List<ApprovalStatusDto> getApprovalStatusSearchList() {
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);

        List<ApprovalStatus> approvalStatusList =
                approvalStatusRepository.findByRecIdNotIn(Arrays.asList(APPROVAL_DRAFT.id(), APPROVAL_CANCELLED.id()));

        return ApprovalStatusMapper.INSTANCE.toApprovalStatusDtoList(approvalStatusList, timeZone);
    }

}
