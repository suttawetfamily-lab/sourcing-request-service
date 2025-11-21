package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.DeptApprovalStatusDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DeptApprovalStatusService {
    List<DeptApprovalStatusDto> getDeptApprovalStatusSearchList();
}
