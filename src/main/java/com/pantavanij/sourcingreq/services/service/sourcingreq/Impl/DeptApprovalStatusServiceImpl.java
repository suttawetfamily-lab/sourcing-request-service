package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.DeptApprovalStatusDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.DeptApprovalStatus;
import com.pantavanij.sourcingreq.services.domain.mapper.DeptApprovalStatusMapper;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.DeptApprovalStatusRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.DeptApprovalStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.pantavanij.sourcingreq.services.enums.DeptApprovalStatus.DEPT_APPROVAL_CANCELLED;
import static com.pantavanij.sourcingreq.services.enums.DeptApprovalStatus.DEPT_APPROVAL_NONE;

@RequiredArgsConstructor
@Service
public class DeptApprovalStatusServiceImpl implements DeptApprovalStatusService {

    private final DeptApprovalStatusRepository deptApprovalStatusRepository;

    @Override
    public List<DeptApprovalStatusDto> getDeptApprovalStatusSearchList() {
        List<String> excludedDeptApprovalStatus = Arrays.asList(DEPT_APPROVAL_NONE.code(), DEPT_APPROVAL_CANCELLED.code());

        List<DeptApprovalStatus> deptApprovalStatusList =
                deptApprovalStatusRepository.findDeptApprovalStatusByNameNotIn(excludedDeptApprovalStatus);
        return DeptApprovalStatusMapper.INSTANCE.toDeptApprovalStatusDto(deptApprovalStatusList);
    }

}
