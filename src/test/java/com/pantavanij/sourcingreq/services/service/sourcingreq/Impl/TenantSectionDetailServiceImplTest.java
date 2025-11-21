package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.request.TenantSectionDetailRequest;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantSectionService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TenantSectionDetailServiceImplTest {

    @InjectMocks
    private TenantSectionDetailServiceImpl tenantSectionDetailService;

    @Mock
    private TenantSectionService tenantSectionService;

    @Mock
    private TenantSectionRepository tenantSectionRepository;

    @Mock
    private TenantSectionDetailRepository tenantSectionDetailRepository;

    @Mock
    private TenantSectionDetailDataSourceRepository tenantSectionDetailDataSourceRepository;

    @Mock
    private TenantSectionDetailDependencyRepository tenantSectionDetailDependencyRepository;

    @Mock
    private TenantSectionDetailValidatorRepository tenantSectionDetailValidatorRepository;

    @Mock
    private TenantSectionDetailWatchRepository tenantSectionDetailWatchRepository;

    @Mock
    private TenantSectionDetailDescriptionRepository tenantSectionDetailDescriptionRepository;

    @Mock
    private TenantService tenantService;
    private TenantSectionDto mockTenantSectionDto;
    private TenantSectionDetail mockTenantSectionDetail;
    private Tenant mockTenant;

    @Before
    public void setUp() {
        mockTenant = Tenant.builder()
                .recId(1)
                .code("TEST")
                .build();

        mockTenantSectionDetail = TenantSectionDetail.builder()
                .id(1L)
                .tenant(mockTenant)
                .fieldName("testField")
                .sequence(1)
                .tenantSectionDetailDependencyList(new ArrayList<>())
                .tenantSectionDetailValidatorList(new ArrayList<>())
                .tenantSectionDetailWatchList(new ArrayList<>())
                .tenantSectionDetailDataSourceList(new ArrayList<>())
                .build();
    }

    @Test
    public void getExportRequestRpt_ShouldReturnList() {
        // Arrange
        List<TenantSectionDetail> mockList = List.of(mockTenantSectionDetail);
        when(tenantSectionDetailRepository.getTenantSectionDetailByTenantAndExportRequestRpt(1, 1))
                .thenReturn(mockList);

        // Act
        List<TenantSectionDetailDto> result = tenantSectionDetailService.getExportRequestRpt(1, 1, 0);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(tenantSectionDetailRepository).getTenantSectionDetailByTenantAndExportRequestRpt(1, 1);
    }

    @Test
    public void isRequiredField_ShouldReturnTrue_WhenFieldExists() {
        // Arrange
        when(tenantSectionDetailRepository.getTenantSectionDetailByTenantAndFieldName(1, "testField", 1, "type"))
                .thenReturn(mockTenantSectionDetail);

        // Act
        boolean result = tenantSectionDetailService.isRequiredField(1, "testField", 1, "type");

        // Assert
        assertTrue(result);
        verify(tenantSectionDetailRepository).getTenantSectionDetailByTenantAndFieldName(1, "testField", 1, "type");
    }

    @Test
    public void createTenantSectionDetail_ShouldCreateNewDetail() {
        // Arrange
        TenantSectionDetailRequest request = new TenantSectionDetailRequest();
        request.setTenantSectionId(1);
        request.setFieldName("testField");
        request.setVisible(true);
        request.setSequence(1);
//        request.setExportItemRptSequence(1);
//        request.setExportRequestRptSequence(1);
        request.setHeaderSequence(1);
        request.setModeViewSequence(1);
        request.setModeViewSequenceSQN(1);
        request.setModeViewSequenceSQV(1);
        request.setModeViewSequenceSQP(1);
        request.setModeViewSequenceSQA(1);

        TenantSectionDto mockTenantSectionDto = new TenantSectionDto();
        mockTenantSectionDto.setId(1L);
        mockTenantSectionDto.setType("REQ");

        TenantSection mockTenantSection = TenantSection.builder()
                .id(1L)
                .type("REQ")
                .build();

        when(tenantService.findByCode(any())).thenReturn(mockTenant);
        when(tenantSectionService.getByRecId(1)).thenReturn(mockTenantSectionDto);
        when(tenantSectionDetailRepository.findFirstByTenantRecIdAndTenantSectionIdOrderBySequenceDesc(anyInt(), anyLong()))
                .thenReturn(Optional.empty());
        when(tenantSectionDetailRepository.save(any())).thenReturn(mockTenantSectionDetail);

        // Act
        TenantSectionDetailDto result = tenantSectionDetailService.createTenantSectionDetail(request);

        // Assert
        assertNotNull(result);
        verify(tenantSectionDetailRepository).save(any());
        verify(tenantSectionService).getByRecId(1);
    }

    @Test
    public void updateTenantSectionDetail_ShouldUpdateExistingDetail() {
        // Arrange
        TenantSectionDetailRequest request = new TenantSectionDetailRequest();
        request.setRecId(1);
        request.setTenantSectionId(1);
        request.setFieldName("updatedField");
        request.setVisible(true);
//        request.setExportItemRpt(true);
//        request.setExportRequestRpt(true);
        request.setHeader(true);
        request.setModeView(true);
        request.setModeViewSQN(true);
        request.setModeViewSQV(true);
        request.setModeViewSQP(true);
        request.setModeViewSQA(true);

        TenantSectionDto mockTenantSectionDto = new TenantSectionDto();
        mockTenantSectionDto.setId(1L);
        mockTenantSectionDto.setType("REQ");

        TenantSection mockTenantSection = TenantSection.builder()
                .id(1L)
                .type("REQ")
                .build();

        when(tenantService.findByCode(any())).thenReturn(mockTenant);
        when(tenantSectionService.getByRecId(1)).thenReturn(mockTenantSectionDto);
        when(tenantSectionDetailRepository.findFirstByTenantAndId(any(), anyLong()))
                .thenReturn(Optional.of(mockTenantSectionDetail));
        when(tenantSectionDetailRepository.save(any())).thenReturn(mockTenantSectionDetail);

        // Act
        TenantSectionDetailDto result = tenantSectionDetailService.updateTenantSectionDetail(request);

        // Assert
        assertNotNull(result);
        verify(tenantSectionDetailRepository).save(any());
        verify(tenantSectionService).getByRecId(1);
    }

    @Test
    public void getExportRequestItemRpt_ShouldReturnList() {
        // Arrange
        List<TenantSectionDetail> mockList = List.of(mockTenantSectionDetail);
        when(tenantSectionDetailRepository.getTenantSectionDetailByTenantAndExportRequestItemRpt(1, 1))
                .thenReturn(mockList);

        // Act
        List<TenantSectionDetailDto> result = tenantSectionDetailService.getExportRequestItemRpt(1, 1, 0);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(tenantSectionDetailRepository).getTenantSectionDetailByTenantAndExportRequestItemRpt(1, 1);
    }
}
