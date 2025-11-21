package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.ApprovalStatusNameDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import org.springframework.stereotype.*;

import java.util.List;

@Service
public interface TenantApprovalStatusService {

    List<ApprovalStatusNameDto> getApprovalStatusSearchList();

    List<TenantApprovalStatus> getApprovalStatus();
}
