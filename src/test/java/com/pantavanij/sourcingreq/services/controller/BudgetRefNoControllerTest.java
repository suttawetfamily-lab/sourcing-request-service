package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponse;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.service.sourcingreq.BudgetRefNoService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BudgetRefNoControllerTest {
    private BudgetRefNoService budgetRefNoService = mock(BudgetRefNoService.class);
    private TenantService tenantService = mock(TenantService.class);
    private BudgetRefNoController budgetRefNoController = new BudgetRefNoController(budgetRefNoService, tenantService);

    @Test
    public void getBudgetRefNoBySearchTerm_success() {
        int tenantId = 1;
        String tenantCode = "TRUE";
        String searchTerm = "BUDGET";

        Tenant mockTenant = Tenant.builder().recId(tenantId).code(tenantCode).build();
        List<OptionDto> budgetRefNoOptions = Arrays.asList(
                OptionDto.builder().value("1").name("BUDGET_1").label("BudgetRefNo 1").build(),
                OptionDto.builder().value("2").name("BUDGET_2").label("BudgetRefNo 2").build(),
                OptionDto.builder().value("3").name("BUDGET_3").label("BudgetRefNo 3").build()
        );

        when(tenantService.findByCode(tenantCode)).thenReturn(mockTenant);
        when(budgetRefNoService.getBudgetRefNoByTenantIdAndSearchTerm(tenantId, searchTerm)).thenReturn(budgetRefNoOptions);

        ResponseEntity actualResult;
        try (MockedStatic mockAppUtil = mockStatic(AppUtil.class)) {
            mockAppUtil.when(AppUtil::getTenantId).thenReturn(tenantCode);
            actualResult = budgetRefNoController.getBudgetRefNoBySearchTerm(searchTerm);
        }

        ResponseEntity expectedResult = ResponseEntity.ok().body(new ApiResponse(budgetRefNoOptions));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getBudgetRefNoBySearchTerm_tenantNotFound() {
        String tenantCode = "TRUE";
        String searchTerm = "BUDGET";

        when(tenantService.findByCode(tenantCode)).thenReturn(null);

        Exception actualResult;
        try (MockedStatic mockedStatic = mockStatic(AppUtil.class)) {
            mockedStatic.when(AppUtil::getTenantId).thenReturn(tenantCode);
            actualResult = assertThrows(BusinessException.class, () -> budgetRefNoController.getBudgetRefNoBySearchTerm(searchTerm));
        }

        assertEquals("E7016: Tenant is not found", actualResult.getMessage());
    }
}