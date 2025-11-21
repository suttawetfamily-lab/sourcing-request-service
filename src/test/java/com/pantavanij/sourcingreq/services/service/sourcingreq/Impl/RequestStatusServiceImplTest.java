package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.RequestStatusDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestStatus;
import com.pantavanij.sourcingreq.services.domain.mapper.RequestStatusMapper;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestStatusRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantApprovalStatusRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantRequestStatusRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestStatusService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import com.pantavanij.sourcingreq.util.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequestStatusServiceImplTest {

    private RequestStatusRepository requestStatusRepository = mock(RequestStatusRepository.class);
    private RequestRepository requestRepository = mock(RequestRepository.class);
    private TenantApprovalStatusRepository tenantApprovalStatusRepository = mock(TenantApprovalStatusRepository.class);
    private TenantRequestStatusRepository tenantRequestStatusRepository = mock(TenantRequestStatusRepository.class);
    private UaaService uaaService = mock(UaaService.class);
    private RequestStatusService requestStatusService = new RequestStatusServiceImpl(
            requestStatusRepository,
            requestRepository,
            tenantApprovalStatusRepository,
            tenantRequestStatusRepository,
            uaaService);

    @Test
    public void getRequestStatusList_success() {
        String mockTimeZone = TestUtil.getMockTimeZone();
        Timestamp mockTimestamp = TestUtil.getMockTimestamp();

        List<RequestStatus> mockRequestStatusList = Arrays.asList(
                RequestStatus.builder().recId(1).name("Draft").createdDate(mockTimestamp).build(),
                RequestStatus.builder().recId(2).name("Awaiting").createdDate(mockTimestamp).build(),
                RequestStatus.builder().recId(3).name("Partial Completed").createdDate(mockTimestamp).build(),
                RequestStatus.builder().recId(4).name("Completed").createdDate(mockTimestamp).build(),
                RequestStatus.builder().recId(5).name("Rejected").createdDate(mockTimestamp).build(),
                RequestStatus.builder().recId(6).name("Cancelled").createdDate(mockTimestamp).build()
        );

        when(requestStatusRepository.findAll()).thenReturn(mockRequestStatusList);
        when(uaaService.getUserTimeZone(any(), null)).thenReturn(mockTimeZone);


        List<RequestStatusDto> actualResult = requestStatusService.getRequestStatusList();

        List<RequestStatusDto> expectedResult =
                RequestStatusMapper.INSTANCE.toRequestStatusDtoList(mockRequestStatusList, mockTimeZone);
        assertEquals(expectedResult, actualResult);
    }

}
