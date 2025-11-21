package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.RequestItemGridFieldDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantRequestItemGridField;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestItemGridFieldService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static com.pantavanij.sourcingreq.services.enums.RequestType.REQUEST_TYPE_CONDITION;
import static com.pantavanij.sourcingreq.services.enums.RequestType.REQUEST_TYPE_QUANTITY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequestItemGridFieldServiceImplTest {
    private TenantRequestItemGridFieldRepository tenantRequestItemGridFieldRepository = mock(TenantRequestItemGridFieldRepository.class);
    private RequestItemGridFieldRepository requestItemGridFieldRepository =mock(RequestItemGridFieldRepository.class);
    private TenantService tenantService = mock(TenantService.class);
    private UaaService uaaService = mock(UaaService.class);
    private TenantOrganizationTemplateRepository tenantOrganizationTemplateRepository = mock(TenantOrganizationTemplateRepository.class);

    private RequestItemGridFieldService requestItemCurrencyService =
            new RequestItemGridFieldServiceImpl(tenantRequestItemGridFieldRepository, requestItemGridFieldRepository, tenantService, uaaService, tenantOrganizationTemplateRepository);

    @Test
    public void typeIsQuantity_getRequestItemGridField_returnQuantityAndAllVisible() {
        String tenantCode = "TRUE";
        Integer typeId = REQUEST_TYPE_QUANTITY.id();

        List<TenantRequestItemGridField> tenantRequestItemGridFieldMock = Arrays.asList(
                TenantRequestItemGridField.builder().code("item").displayName("Item").visible(true).build(),
                TenantRequestItemGridField.builder().code("quantity").displayName("Quantity").visible(true).build(),
                TenantRequestItemGridField.builder().code("condition").displayName("Condition").visible(true).build()
        );

        List<RequestItemGridFieldDto> expectedResult = Arrays.asList(
                RequestItemGridFieldDto.builder().code("item").displayName("Item").build(),
                RequestItemGridFieldDto.builder().code("quantity").displayName("Quantity").build()
        );


        when(tenantRequestItemGridFieldRepository.findByTenant_CodeAndVisibleOrderBySequence(tenantCode, true))
                .thenReturn(tenantRequestItemGridFieldMock);

        List<RequestItemGridFieldDto> actualResult = requestItemCurrencyService.getRequestItemGridField("SQN", tenantCode, typeId, 9);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void typeIsCondition_getRequestItemGridField_returnConditionAndAllVisible() {
        String tenantCode = "TRUE";
        Integer typeId = REQUEST_TYPE_CONDITION.id();

        List<TenantRequestItemGridField> tenantRequestItemGridFieldMock = Arrays.asList(
                TenantRequestItemGridField.builder().code("item").displayName("Item").visible(true).build(),
                TenantRequestItemGridField.builder().code("quantity").displayName("Quantity").visible(true).build(),
                TenantRequestItemGridField.builder().code("condition").displayName("Condition").visible(true).build()
        );

        List<RequestItemGridFieldDto> expectedResult = Arrays.asList(
                RequestItemGridFieldDto.builder().code("item").displayName("Item").build(),
                RequestItemGridFieldDto.builder().code("condition").displayName("Condition").build()
        );


        when(tenantRequestItemGridFieldRepository.findByTenant_CodeAndVisibleOrderBySequence(tenantCode, true))
                .thenReturn(tenantRequestItemGridFieldMock);

        List<RequestItemGridFieldDto> actualResult = requestItemCurrencyService.getRequestItemGridField("SQN", tenantCode, typeId, 0);

        assertEquals(expectedResult, actualResult);
    }
}