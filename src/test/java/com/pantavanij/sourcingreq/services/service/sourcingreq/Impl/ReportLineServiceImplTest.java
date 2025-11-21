package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.EpAuthClient;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthReviewerResponse;
import com.pantavanij.sourcingreq.services.domain.response.ReportLineResponse;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.ReportLineRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReportLineServiceImplTest {

    @InjectMocks
    private ReportLineServiceImpl reportLineService;

    @Mock
    private ReportLineRepository reportLineRepository;

    @Mock
    private UaaService uaaService;

    @Mock
    private EPAuthService epAuthService;

    @Mock
    private TenantService tenantService;

    @Mock
    private TenantConfigService tenantConfigService;

    @Mock
    private EpAuthClient epAuthClient;

    private UserDto mockUserDto;
    private Tenant mockTenant;

    @Before
    public void setup() {
        mockUserDto = new UserDto();
        mockUserDto.setUsername("testUser");
        mockUserDto.setTenantId("TEST_TENANT");
        mockUserDto.setIdp("TEST_IDP");

        mockTenant = new Tenant();
        mockTenant.setRecId(1);
        mockTenant.setCode("TEST_TENANT");
    }

    @Test
    public void testGetRequestReportLine() {
        // Prepare test data
        ReportLineSearchRequest searchRequest = new ReportLineSearchRequest();
        searchRequest.setExceptReportLines(new ArrayList<>());
        Pageable pageable = PageRequest.of(0, 10);

        List<ReportLine> reportLines = new ArrayList<>();
        ReportLine reportLine = new ReportLine();
        reportLine.setRecId(1L);
        reportLines.add(reportLine);

        Page<ReportLine> reportLinePage = new PageImpl<>(reportLines);

        // Mock repository response
        when(reportLineRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(reportLinePage);

        // Execute test
        ReportLineResponse response = reportLineService.getRequestReportLine(searchRequest, pageable);

        // Verify results
        assertNotNull(response);
        assertEquals(1, response.getTotal());
        assertEquals(1, response.getTotalPage());
    }

    @Test
    public void testCreateReportLine() {
        try (MockedStatic<AppUtil> appUtilMock = mockStatic(AppUtil.class)) {
            // Mock static methods
            appUtilMock.when(AppUtil::getUser).thenReturn(mockUserDto);
            appUtilMock.when(AppUtil::getUserName).thenReturn("testUser");
            appUtilMock.when(AppUtil::getTenantId).thenReturn("TEST_TENANT");

            // Prepare test data
            ReportLineRequest request = new ReportLineRequest();
            request.setReportLineName("Test Report Line");
            request.setEmail("test@test.com");
            request.setSequence(1);

            // Mock dependencies
            when(tenantService.findByCode(anyString())).thenReturn(mockTenant);
            when(uaaService.getUserTimeZone(any(), any())).thenReturn("UTC");
            when(reportLineRepository.findFirstByTenantRecIdOrderBySequenceDesc(any()))
                .thenReturn(Optional.empty());
            when(reportLineRepository.save(any(ReportLine.class)))
                .thenAnswer(i -> i.getArguments()[0]);

            // Execute test
            ReportLineDto result = reportLineService.createReportLine(request);

            // Verify results
            assertNotNull(result);
            assertEquals(request.getReportLineName(), result.getReportLineName());
            assertEquals(request.getEmail(), result.getEmail());
        }
    }

    @Test
    public void testDeleteReportLine() {
        // Mock dependencies
        when(tenantService.findByCode(anyString())).thenReturn(mockTenant);
        when(reportLineRepository.findReportLineByRecIdAndTenant(anyInt(), anyInt()))
            .thenReturn(Optional.of(new ReportLine()));

        try (MockedStatic<AppUtil> appUtilMock = mockStatic(AppUtil.class)) {
            appUtilMock.when(AppUtil::getTenantId).thenReturn("TEST_TENANT");

            // Execute test
            boolean result = reportLineService.deleteReportLine(1);

            // Verify results
            assertTrue(result);
            verify(reportLineRepository).deleteByRecIdAndTenant(eq(1L), any(Tenant.class));
            verify(reportLineRepository).reOrderSequenceByTenantRecId(any());
        }
    }

    @Test
    public void testFindReportLineByRecId() {
        try (MockedStatic<AppUtil> appUtilMock = mockStatic(AppUtil.class)) {
            // Mock static methods
            appUtilMock.when(AppUtil::getUser).thenReturn(mockUserDto);
            appUtilMock.when(AppUtil::getTenantId).thenReturn("TEST_TENANT");

            // Prepare test data
            ReportLine reportLine = new ReportLine();
            reportLine.setRecId(1L);
            reportLine.setReportLineName("Test Report Line");

            // Mock dependencies
            when(tenantService.findByCode(anyString())).thenReturn(mockTenant);
            when(uaaService.getUserTimeZone(any(), any())).thenReturn("UTC");
            when(reportLineRepository.findReportLineByRecIdAndTenant(anyInt(), any()))
                .thenReturn(Optional.of(reportLine));

            // Execute test
            ReportLineDto result = reportLineService.findReportLineByRecId(1);

            // Verify results
            assertNotNull(result);
            assertEquals(reportLine.getReportLineName(), result.getReportLineName());
        }
    }
}
