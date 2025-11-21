package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.EpAuthClient;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Approver;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.ApproverRequest;
import com.pantavanij.sourcingreq.services.domain.request.ApproverSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.ConditionSearchRequest;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.ApproverRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApproverServiceImplTest {

    @InjectMocks
    private ApproverServiceImpl approverService;

    @Mock
    private ApproverRepository approverRepository;

    @Mock
    private UaaService uaaService;

    @Mock
    private TenantService tenantService;

    @Mock
    private EpAuthClient epAuthClient;

    private Tenant mockTenant;
    private UserDto mockUserDto;
    private Approver mockApprover;

    @Before
    public void setUp() {
        mockTenant = Tenant.builder()
                .recId(1)
                .code("TEST")
                .build();

        mockUserDto = new UserDto();
        mockUserDto.setUsername("testUser");
        mockUserDto.setTenantId("TEST");

        mockApprover = Approver.builder()
                .recId(1)
                .tenant(mockTenant)
                .userId(1)
                .approverName("Test Approver")
                .email("test@test.com")
                .phone("1234567890")
                .sequence(1)
                .isDefault(true)
                .active(true)
                .build();
    }

    @Test
    public void testSaveApprover() {
        try (MockedStatic<AppUtil> appUtilMockedStatic = mockStatic(AppUtil.class);
             MockedStatic<DateTimeUtil> dateTimeMockedStatic = mockStatic(DateTimeUtil.class)) {

            appUtilMockedStatic.when(AppUtil::getUser).thenReturn(mockUserDto);
            dateTimeMockedStatic.when(DateTimeUtil::getTimestampUTC).thenReturn(new Timestamp(System.currentTimeMillis()));

            when(tenantService.findByCode(anyString())).thenReturn(mockTenant);
            when(approverRepository.findApproverByTenantAndUserId(any(), anyInt())).thenReturn(Optional.empty());
            when(approverRepository.save(any(Approver.class))).thenReturn(mockApprover);

            ApproverDto approverDto = new ApproverDto();
            approverDto.setUserId(1);
            approverDto.setApproverName("Test Approver");
            approverDto.setEmail("test@test.com");
            approverDto.setPhone("1234567890");

            Integer result = approverService.saveApprover(approverDto);

            assertNotNull(result);
            assertEquals(mockApprover.getRecId(), result);
        }
    }

    @Test
    public void testGetRequestApprover() {
        List<Approver> approvers = new ArrayList<>();
        approvers.add(mockApprover);
        Page<Approver> approverPage = new PageImpl<>(approvers);

        ApproverSearchRequest searchRequest = new ApproverSearchRequest();
        searchRequest.setExceptApprovers(new ArrayList<>());
        Pageable pageable = PageRequest.of(0, 10);

        when(approverRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(approverPage);

        var response = approverService.getRequestApprover(searchRequest, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotal());
        assertEquals(1, response.getData().size());
    }

    @Test
    public void testFindApproverByRecId() {
        try (MockedStatic<AppUtil> appUtilMockedStatic = mockStatic(AppUtil.class)) {
            appUtilMockedStatic.when(AppUtil::getTenantId).thenReturn("TEST");
            appUtilMockedStatic.when(AppUtil::getUser).thenReturn(mockUserDto);

            when(tenantService.findByCode(anyString())).thenReturn(mockTenant);
            when(approverRepository.findApproverByRecIdAndTenant(anyInt(), anyInt()))
                    .thenReturn(Optional.of(mockApprover));
            when(uaaService.getUserTimeZone(any(), any())).thenReturn("UTC");

            ApproverDto result = approverService.findApproverByRecId(1);

            assertNotNull(result);
            assertEquals(mockApprover.getApproverName(), result.getApproverName());
        }
    }

    @Test
    public void testCreateApprover() {
        try (MockedStatic<AppUtil> appUtilMockedStatic = mockStatic(AppUtil.class);
             MockedStatic<DateTimeUtil> dateTimeMockedStatic = mockStatic(DateTimeUtil.class)) {

            appUtilMockedStatic.when(AppUtil::getTenantId).thenReturn("TEST");
            appUtilMockedStatic.when(AppUtil::getUser).thenReturn(mockUserDto);
            appUtilMockedStatic.when(AppUtil::getUserName).thenReturn("testUser");
            dateTimeMockedStatic.when(DateTimeUtil::getTimestampUTC).thenReturn(new Timestamp(System.currentTimeMillis()));

            when(tenantService.findByCode(anyString())).thenReturn(mockTenant);
            when(approverRepository.findFirstByTenantRecIdOrderBySequenceDesc(anyInt()))
                    .thenReturn(Optional.of(mockApprover));
            when(approverRepository.save(any(Approver.class))).thenReturn(mockApprover);
            when(uaaService.getUserTimeZone(any(), any())).thenReturn("UTC");

            ApproverRequest request = new ApproverRequest();
            request.setApproverName("Test Approver");
            request.setEmail("test@test.com");
            request.setPhone("1234567890");

            ApproverDto result = approverService.createApprover(request);

            assertNotNull(result);
            assertEquals(mockApprover.getApproverName(), result.getApproverName());
        }
    }

    @Test
    public void testDeleteApprover() {
        try (MockedStatic<AppUtil> appUtilMockedStatic = mockStatic(AppUtil.class)) {
            appUtilMockedStatic.when(AppUtil::getTenantId).thenReturn("TEST");

            when(tenantService.findByCode(anyString())).thenReturn(mockTenant);
            when(approverRepository.findApproverByRecIdAndTenant(anyInt(), anyInt()))
                    .thenReturn(Optional.of(mockApprover));
            doNothing().when(approverRepository).deleteApproverByRecId(anyInt());

            boolean result = approverService.deleteApprover(1);

            assertTrue(result);
            verify(approverRepository, times(1)).deleteApproverByRecId(1);
        }
    }
}
