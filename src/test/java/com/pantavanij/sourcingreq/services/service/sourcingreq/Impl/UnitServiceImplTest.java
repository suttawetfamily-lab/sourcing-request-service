package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.request.UnitMasterDataRequest;
import com.pantavanij.sourcingreq.services.domain.request.UnitSearchRequest;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitServiceImplTest {

    @Mock
    private TenantService tenantService;

    @Mock
    private TenantUnitRepository tenantUnitRepository;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private UaaService uaaService;

    @InjectMocks
    private UnitServiceImpl unitService;

    private Unit testUnit;
    private UnitMasterDataRequest testRequest;

    @BeforeEach
    void setUp() {
        testUnit = Unit.builder()
                .recId(1)
                .code("TEST001")
                .name("Test Unit")
                .build();

        testRequest = new UnitMasterDataRequest();
        testRequest.setRecId(1);
        testRequest.setCode("TEST001");
        testRequest.setName("Test Unit");
    }

    @Test
    void getUnitByTenantIdV1_Success() {
        when(unitRepository.getUnitByTenantId(1, 2)).thenReturn(List.of(testUnit));

        List<UnitDto> result = unitService.getUnitByTenantIdV1(1, 2);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUnit.getCode(), result.get(0).getUnitCode());
    }

    @Test
    void getUnitByTenantIdV1_EmptyResult() {
        when(unitRepository.getUnitByTenantId(1, 2)).thenReturn(new ArrayList<>());

        List<UnitDto> result = unitService.getUnitByTenantIdV1(1, 2);

        assertNull(result);
    }

    @Test
    void createUnit_Success() {
        when(unitRepository.save(any(Unit.class))).thenReturn(testUnit);

        Integer result = unitService.createUnit(testRequest);

        assertNotNull(result);
        assertEquals(testUnit.getRecId(), result);
    }

    @Test
    void updateUnit_Success() {
        when(unitRepository.findUnitByRecId(1)).thenReturn(Optional.of(testUnit));
        when(unitRepository.save(any(Unit.class))).thenReturn(testUnit);

        Integer result = unitService.updateUnit(testRequest);

        assertEquals(testUnit.getRecId(), result);
        verify(unitRepository).save(any(Unit.class));
    }

    @Test
    void deleteUnitById_Success() {
        Tenant tenant = new Tenant();
        tenant.setRecId(1);

        when(tenantService.findByCode(any())).thenReturn(tenant);
        when(unitRepository.findUnitByRecId(1)).thenReturn(Optional.of(testUnit));
        when(tenantUnitRepository.findByTenantRecIdAndUnitRecId(1, 1)).thenReturn(Optional.empty());

        Integer result = unitService.deleteUnitById(1);

        assertEquals(1, result);
        verify(unitRepository).deleteByRecId(1);
    }

    @Test
    void searchUnitsByCondition_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Unit> units = List.of(testUnit);

        when(unitRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(units, pageable, units.size()));
        when(uaaService.getUserTimeZone(any(), any())).thenReturn("UTC");

        UnitSearchDto result = unitService.searchUnitsByCondition(new UnitSearchRequest(), pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getUnitList().size());
    }

    @Test
    void getUnitByUnitCode_Success() {
        when(unitRepository.getUnitByUnitCode("TEST001")).thenReturn(Optional.of(testUnit));

        Optional<Unit> result = unitService.getUnitByUnitCode("TEST001");

        assertTrue(result.isPresent());
        assertEquals("TEST001", result.get().getCode());
    }
}
