package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.RequestStatusNameDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantRequestStatus;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantRequestStatusRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantConfigService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantRequestStatusService;
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
public class TenantRequestStatusServiceImplTest {

    private TenantRequestStatusRepository tenantRequestStatusRepository = mock(TenantRequestStatusRepository.class);
    private TenantConfigService tenantConfigService = mock(TenantConfigService.class);
    private TenantService tenantService = mock(TenantService.class);
    private TenantRequestStatusService tenantRequestStatusService = new TenantRequestStatusServiceImpl(tenantRequestStatusRepository, tenantConfigService, tenantService);

    @Test
    public void getRequestStatusList() {
        String tenantCode = "TRUE";
        Timestamp mockTimestamp = TestUtil.getMockTimestamp();

        List<TenantRequestStatus> mocktenantRequestStatusList = Arrays.asList(
                TenantRequestStatus.builder()
                        .name("Draft")
                        .canEdit(true)
                        .canDelete(true)
                        .canDuplicate(true)
                        .canCancel(false)
                        .canCopyToPR(false)
                        .canViewHistory(false)
                        .createdBy("Admin")
                        .createdDate(mockTimestamp)
                        .build(),
                TenantRequestStatus.builder()
                        .name("Awaiting")
                        .canEdit(false)
                        .canDelete(false)
                        .canDuplicate(true)
                        .canCancel(true)
                        .canCopyToPR(false)
                        .canViewHistory(true)
                        .createdBy("Admin")
                        .createdDate(mockTimestamp)
                        .build(),
                TenantRequestStatus.builder()
                        .name("Completed")
                        .canEdit(false)
                        .canDelete(false)
                        .canDuplicate(true)
                        .canCancel(false)
                        .canCopyToPR(true)
                        .canViewHistory(true)
                        .createdBy("Admin")
                        .createdDate(mockTimestamp)
                        .build()
        );

        when(tenantRequestStatusRepository.findByTenant_Code(tenantCode)).thenReturn(mocktenantRequestStatusList);

        List<RequestStatusNameDto> actualResult;
        try (MockedStatic mockAppUtil = mockStatic(AppUtil.class)) {
            mockAppUtil.when(AppUtil::getTenantId).thenReturn(tenantCode);
            actualResult = tenantRequestStatusService.getRequestStatusList();
        }

        List<RequestStatusNameDto> expectedResult = Arrays.asList(
                RequestStatusNameDto.builder().recId(1).name("Draft").build(),
                RequestStatusNameDto.builder().recId(2).name("Awaiting").build(),
                RequestStatusNameDto.builder().recId(3).name("Completed").build()
        );

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getReviewStatusList() {
        String tenantCode = "TRUE";
        Timestamp mockTimestamp = TestUtil.getMockTimestamp();
        List<String> excludedApprovalStatus = Arrays.asList(TENANT_APPROVAL_DRAFT.code(), TENANT_APPROVAL_PENDING.code());

        List<TenantRequestStatus> mockTenantRequestStatusList = Arrays.asList(
                TenantRequestStatus.builder()
                        .name("Awaiting")
                        .canEdit(false)
                        .canDelete(false)
                        .canDuplicate(true)
                        .canCancel(true)
                        .canCopyToPR(false)
                        .canViewHistory(true)
                        .createdBy("Admin")
                        .createdDate(mockTimestamp)
                        .build(),
                TenantRequestStatus.builder()
                        .name("Completed")
                        .canEdit(false)
                        .canDelete(false)
                        .canDuplicate(true)
                        .canCancel(false)
                        .canCopyToPR(true)
                        .canViewHistory(true)
                        .createdBy("Admin")
                        .createdDate(mockTimestamp)
                        .build()
        );

        when(tenantRequestStatusRepository.findByTenant_CodeAndNameNotIn(tenantCode, excludedApprovalStatus))
                .thenReturn(mockTenantRequestStatusList);

        List<RequestStatusNameDto> actualResult;
        try (MockedStatic mockAppUtil = mockStatic(AppUtil.class)) {
            mockAppUtil.when(AppUtil::getTenantId).thenReturn(tenantCode);
            actualResult = tenantRequestStatusService.getReviewStatusList();
        }

        List<RequestStatusNameDto> expectedResult = Arrays.asList(
                RequestStatusNameDto.builder().recId(2).name("Awaiting").build(),
                RequestStatusNameDto.builder().recId(3).name("Completed").build()
        );

        assertEquals(expectedResult, actualResult);
    }

}