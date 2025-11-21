package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.BudgetRefNo;
import com.pantavanij.sourcingreq.services.domain.mapper.BudgetRefNoMapper;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.BudgetRefNoRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.BudgetRefNoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BudgetRefNoServiceImplTest {

    private BudgetRefNoRepository budgetRefNoRepository = mock(BudgetRefNoRepository.class);
    private BudgetRefNoService budgetRefNoService = new BudgetRefNoServiceImpl(budgetRefNoRepository);

    @Test
    public void getBudgetRefNoByTenantIdAndSearchTerm_success() {
        int tenantId = 1;
        String searchTerm = "BUDGET";

        List<BudgetRefNo> budgetRefNoMock = Arrays.asList(
                BudgetRefNo.builder().recId(1).code("BUDGET_1").name("BudgetRefNo 1").build(),
                BudgetRefNo.builder().recId(2).code("BUDGET_2").name("BudgetRefNo 2").build(),
                BudgetRefNo.builder().recId(3).code("BUDGET_3").name("BudgetRefNo 3").build()
        );

        when(budgetRefNoRepository.findByTenantIdAndCodeOrName(tenantId, searchTerm))
                .thenReturn(budgetRefNoMock);

        List<OptionDto> actualResult = budgetRefNoService.getBudgetRefNoByTenantIdAndSearchTerm(tenantId, searchTerm);

        List<OptionDto> expectedResult = BudgetRefNoMapper.INSTANCE.toBudgetRefNoOptionDto(budgetRefNoMock);
        assertEquals(expectedResult, actualResult);
    }

}