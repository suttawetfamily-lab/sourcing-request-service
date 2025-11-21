package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.ApprovalStatusNameDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantApprovalStatus;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantApprovalStatusRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantApprovalStatusService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantConfigService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.util.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static com.pantavanij.sourcingreq.services.enums.TenantApprovalStatus.TENANT_APPROVAL_DRAFT;
import static com.pantavanij.sourcingreq.services.enums.TenantApprovalStatus.TENANT_APPROVAL_PENDING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TenantApprovalStatusServiceImplTest {

    private final TenantApprovalStatusRepository tenantApprovalStatusRepository = mock(TenantApprovalStatusRepository.class);
    private final TenantConfigService tenantConfigService = mock(TenantConfigService.class);
    private final TenantService tenantService = mock(TenantService.class);
    private final TenantApprovalStatusService tenantApprovalStatusService = new TenantApprovalStatusServiceImpl(tenantApprovalStatusRepository, tenantConfigService, tenantService);

    @Test
    public void getApprovalStatusSearchList() {
        String tenantCode = "TRUE";
        Timestamp mockTimestamp = TestUtil.getMockTimestamp();
        List<String> mockExcludedStatus = Arrays.asList(TENANT_APPROVAL_DRAFT.code(), TENANT_APPROVAL_PENDING.code());

        List<TenantApprovalStatus> mockTenantRequestStatusList = Arrays.asList(
                TenantApprovalStatus.builder()
                        .name("Draft")
                        .canEdit(false)
                        .canDelete(false)
                        .canDuplicate(false)
                        .canCancel(false)
                        .canCopyToPR(false)
                        .canViewHistory(false)
                        .createdBy("Admin")
                        .createdDate(mockTimestamp)
                        .build(),
                TenantApprovalStatus.builder()
                        .name("Awaiting")
                        .canEdit(false)
                        .canDelete(false)
                        .canDuplicate(false)
                        .canCancel(false)
                        .canCopyToPR(false)
                        .canViewHistory(true)
                        .createdBy("Admin")
                        .createdDate(mockTimestamp)
                        .build(),
                TenantApprovalStatus.builder()
                        .name("Completed")
                        .canEdit(false)
                        .canDelete(false)
                        .canDuplicate(false)
                        .canCancel(false)
                        .canCopyToPR(false)
                        .canViewHistory(true)
                        .createdBy("Admin")
                        .createdDate(mockTimestamp)
                        .build()
        );

        when(tenantApprovalStatusRepository.findByTenant_CodeAndNameNotIn(tenantCode, mockExcludedStatus))
                .thenReturn(mockTenantRequestStatusList);

        List<ApprovalStatusNameDto> actualResult;
        try (MockedStatic mockAppUtil = mockStatic(AppUtil.class)) {
            mockAppUtil.when(AppUtil::getTenantId).thenReturn(tenantCode);
            actualResult = tenantApprovalStatusService.getApprovalStatusSearchList();
        }

        List<ApprovalStatusNameDto> expectedResult = Arrays.asList(
                ApprovalStatusNameDto.builder().recId(1).name("Draft").build(),
                ApprovalStatusNameDto.builder().recId(2).name("Awaiting").build(),
                ApprovalStatusNameDto.builder().recId(3).name("Completed").build()
        );

        assertEquals(expectedResult, actualResult);
    }

}