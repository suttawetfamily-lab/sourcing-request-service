package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.ApprovalStatusDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ApprovalStatus;
import com.pantavanij.sourcingreq.services.domain.mapper.ApprovalStatusMapper;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.ApprovalStatusRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.ApprovalStatusService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import com.pantavanij.sourcingreq.util.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static com.pantavanij.sourcingreq.services.enums.ApprovalStatus.APPROVAL_CANCELLED;
import static com.pantavanij.sourcingreq.services.enums.ApprovalStatus.APPROVAL_DRAFT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApprovalStatusServiceImplTest {

    private ApprovalStatusRepository approvalStatusRepository = mock(ApprovalStatusRepository.class);
    private UaaService uaaService = mock(UaaService.class);
    private ApprovalStatusService actionPrivilegeService =
            new ApprovalStatusServiceImpl(approvalStatusRepository, uaaService);

    @Test
    public void getApprovalStatusSearchList_success() {
        String mockTimeZone = TestUtil.getMockTimeZone();
        Timestamp mockTimestamp = TestUtil.getMockTimestamp();

        List<ApprovalStatus> mockApprovalStatusList = Arrays.asList(
                ApprovalStatus.builder().recId(1).name("Awaiting").createdDate(mockTimestamp).build(),
                ApprovalStatus.builder().recId(2).name("Partial Completed").createdDate(mockTimestamp).build(),
                ApprovalStatus.builder().recId(3).name("Completed").createdDate(mockTimestamp).build(),
                ApprovalStatus.builder().recId(4).name("Rejected").createdDate(mockTimestamp).build(),
                ApprovalStatus.builder().recId(5).name("Cancelled").createdDate(mockTimestamp).build()
        );

        when(approvalStatusRepository.findByRecIdNotIn(Arrays.asList(APPROVAL_DRAFT.id(), APPROVAL_CANCELLED.id())))
                .thenReturn(mockApprovalStatusList);
        when(uaaService.getUserTimeZone(any(), null)).thenReturn(mockTimeZone);

        List<ApprovalStatusDto> actualResult = actionPrivilegeService.getApprovalStatusSearchList();

        List<ApprovalStatusDto> expectedResult =
                ApprovalStatusMapper.INSTANCE.toApprovalStatusDtoList(mockApprovalStatusList, mockTimeZone);
        assertEquals(expectedResult, actualResult);
    }

}